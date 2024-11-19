

import pika
import time

# RabbitMQ Configuration
QUEUE_NAME = "documentQueue"
EXCHANGE_NAME = "documentExchange"
ROUTING_KEY = "document.routingKey"
RABBITMQ_HOST = "localhost"  # Use the service name defined in docker-compose
RABBITMQ_PORT = 15672        # Default port for RabbitMQ

# Callback function to handle incoming messages
def on_message(channel, method, properties, body):
    message = body.decode('utf-8')  # Decode message as string
    print(f"Received message: {message}")

    # Process the message (extract document name if possible)
    if message.startswith("Document created: "):
        document_name = message.replace("Document created: ", "")
        print(f"Extracted Document Name: {document_name}")
    else:
        print("Unrecognized message format")

    # Acknowledge message
    channel.basic_ack(delivery_tag=method.delivery_tag)

def start_ocr_worker():
    retry_interval = 5  # Retry every 5 seconds
    while True:
        try:
            # Connect to RabbitMQ
            connection = pika.BlockingConnection(pika.ConnectionParameters(host="localhost", port=5672))
            channel = connection.channel()

            # Declare the queue (to ensure it exists)
            channel.queue_declare(queue="documentQueue", durable=False)
            channel.queue_bind(exchange="documentExchange", queue="documentQueue", routing_key="document.routingKey")

            print("Connected to RabbitMQ and listening for messages...")
            channel.basic_consume(queue="documentQueue", on_message_callback=on_message)
            channel.start_consuming()

        except pika.exceptions.AMQPConnectionError as e:
            print(f"Connection failed: {e}. Retrying in {retry_interval} seconds...")
            time.sleep(retry_interval)

        except KeyboardInterrupt:
            print("Stopped by user.")
            break

        finally:
            if connection:
                connection.close()

if __name__ == "__main__":
    start_ocr_worker()