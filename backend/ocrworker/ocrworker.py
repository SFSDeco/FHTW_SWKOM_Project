import pika
import time
import os
import logging
import json
from pdf2image import convert_from_bytes
import pytesseract
from minio import Minio
from elasticsearch import Elasticsearch

from io import BytesIO

# Configuration variables
QUEUE_NAME = "documentQueue"
EXCHANGE_NAME = "documentExchange"
ROUTING_KEY = "document.routingKey"
RABBITMQ_HOST = os.getenv('RABBITMQ_HOST', 'localhost')
RABBITMQ_PORT = int(os.getenv('RABBITMQ_PORT', 5672))

MINIO_HOST = os.getenv('MINIO_HOST', 'localhost')
MINIO_PORT = int(os.getenv('MINIO_PORT', 9000))
MINIO_ACCESS_KEY = os.getenv('MINIO_ACCESS_KEY', 'minioadmin')
MINIO_SECRET_KEY = os.getenv('MINIO_SECRET_KEY', 'minioadmin')
MINIO_BUCKET_NAME = 'documents'

ELASTICSEARCH_HOST = os.getenv('ELASTICSEARCH_HOST', 'localhost')
ELASTICSEARCH_PORT = int(os.getenv('ELASTICSEARCH_PORT', 9200))

logging.basicConfig(
    format='%(asctime)s - %(levelname)s - %(message)s',
    level=logging.INFO
)

# MinIO client setup
minio_client = Minio(
    f'{MINIO_HOST}:{MINIO_PORT}',
    access_key=MINIO_ACCESS_KEY,
    secret_key=MINIO_SECRET_KEY,
    secure=False
)

# Elasticsearch client setup
es = Elasticsearch([{'scheme': 'http', 'host': ELASTICSEARCH_HOST, 'port': ELASTICSEARCH_PORT}])

# Process PDF using Tesseract
def process_pdf_with_tesseract(pdf_data):
    try:
        images = convert_from_bytes(pdf_data)
        full_text = ""
        for i, image in enumerate(images):
            logging.info(f"Processing page {i + 1}")
            text = pytesseract.image_to_string(image)
            full_text += text

        return full_text
    except Exception as e:
        logging.error(f"Error processing PDF with Tesseract: {e}")
        return ""

# Insert extracted text into Elasticsearch
def insert_into_elasticsearch(doc_id, text, document_name):
    try:
        document = {
            "id": doc_id,           # Store the document ID
            "name": document_name,  # Store the document name
            "text": text,           # The extracted text
            "status": "processed",
            "source": "ocr_worker"
        }
        # Index the document using its ID as the Elasticsearch document ID
        es.index(index="documents", id=doc_id, document=document)
        logging.info(f"Inserted document with ID {doc_id} and name '{document_name}' into Elasticsearch.")
    except Exception as e:
        logging.error(f"Error inserting into Elasticsearch: {e}")

# Download file from MinIO
def download_file_from_minio(file_name):
    try:
        response = minio_client.get_object(MINIO_BUCKET_NAME, file_name)
        logging.info(f"Downloaded file {file_name} from MinIO.")
        return response.read()
    except Exception as e:
        logging.error(f"Error downloading file from MinIO: {e}")
        return None

# Handle incoming RabbitMQ messages
def on_message(channel, method, properties, body):
    try:
        # Deserialize the JSON message into a DocumentDTO
        document_dto = json.loads(body.decode('utf-8'))
        logging.info(f"Received DocumentDTO: {document_dto}")

        # Extract document ID and name
        document_id = document_dto.get("id")
        document_name = document_dto.get("name")

        if not document_id or not document_name:
            logging.error("Invalid DocumentDTO: Missing 'id' or 'name'")
            channel.basic_ack(delivery_tag=method.delivery_tag)
            return

        # Reconstruct the file name based on ID and name
        file_name = f"{document_id}_{document_name}"
        logging.info(f"Processing file: {file_name}")

        # Download file from MinIO
        pdf_data = download_file_from_minio(file_name)
        if pdf_data:
            # Process the PDF to extract text
            extracted_text = process_pdf_with_tesseract(pdf_data)
            # Insert text into Elasticsearch
            insert_into_elasticsearch(document_id, extracted_text, document_name)
        else:
            logging.error(f"Failed to download file {file_name} from MinIO.")

        channel.basic_ack(delivery_tag=method.delivery_tag)

    except json.JSONDecodeError as e:
        logging.error(f"Failed to decode message: {e}")
        channel.basic_ack(delivery_tag=method.delivery_tag)
    except Exception as e:
        logging.error(f"Error processing message: {e}")
        channel.basic_ack(delivery_tag=method.delivery_tag)

# Start the OCR Worker
def start_ocr_worker():
    retry_interval = 5
    connection = None
    while True:
        try:
            connection = pika.BlockingConnection(
                pika.ConnectionParameters(host=RABBITMQ_HOST, port=RABBITMQ_PORT)
            )
            channel = connection.channel()
            channel.queue_declare(queue=QUEUE_NAME, durable=False)
            channel.queue_bind(exchange=EXCHANGE_NAME, queue=QUEUE_NAME, routing_key=ROUTING_KEY)
            logging.info("Connected to RabbitMQ and listening for messages...")
            channel.basic_consume(queue=QUEUE_NAME, on_message_callback=on_message)
            channel.start_consuming()
        except pika.exceptions.AMQPConnectionError as e:
            logging.error(f"Connection failed: {e}. Retrying in {retry_interval} seconds...")
            time.sleep(retry_interval)
        except KeyboardInterrupt:
            logging.info("Stopped by user.")
            break
        finally:
            if connection:
                connection.close()

if __name__ == "__main__":
    logging.info("Starting OCR Worker...")
    start_ocr_worker()
