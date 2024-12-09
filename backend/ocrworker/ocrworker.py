import pika
import time
import os
import logging
from pdf2image import convert_from_path, convert_from_bytes
import pytesseract
from PIL import Image
from minio import Minio
from elasticsearch import Elasticsearch
from io import BytesIO

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

minio_client = Minio(
    f'{MINIO_HOST}:{MINIO_PORT}',
    access_key=MINIO_ACCESS_KEY,
    secret_key=MINIO_SECRET_KEY,
    secure=False
)

es = Elasticsearch([{'scheme': 'http', 'host': ELASTICSEARCH_HOST, 'port': ELASTICSEARCH_PORT}])

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

def insert_into_elasticsearch(doc_id, text):
    try:
        document = {
            "text": text,
            "status": "processed",
            "source": "ocr_worker"
        }
        es.index(index="documents", id=doc_id, document=document)
        logging.info(f"Inserted document with ID {doc_id} into Elasticsearch.")
    except Exception as e:
        logging.error(f"Error inserting into Elasticsearch: {e}")

def download_file_from_minio(file_name):
    try:
        response = minio_client.get_object(MINIO_BUCKET_NAME, file_name)
        logging.info(f"Downloaded file {file_name} from Minio.")
        return response.read()
    except Exception as e:
        logging.error(f"Error downloading file from Minio: {e}")
        return None

def on_message(channel, method, properties, body):
    try:
        message = body.decode('utf-8')
        logging.info(f"Received message: {message}")
        if message.startswith("Document created: "):
            document_name = message.replace("Document created: ", "")
            logging.info(f"Extracted Document Name: {document_name}")
            pdf_data = download_file_from_minio(document_name)
            if pdf_data:
                extracted_text = process_pdf_with_tesseract(pdf_data)
                insert_into_elasticsearch(document_name, extracted_text)
            else:
                logging.error(f"Failed to process {document_name} as it could not be downloaded from Minio.")
        else:
            logging.error("Unrecognized message format")
        channel.basic_ack(delivery_tag=method.delivery_tag)
    except Exception as e:
        logging.error(f"Error processing message: {e}")

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
