FROM python:3.9-slim

WORKDIR /app

COPY . /app

RUN apt-get update && \
    apt-get install -y \
    poppler-utils \
    tesseract-ocr \
    && apt-get clean

RUN python -m venv .venv && \
    .venv/bin/pip install --upgrade pip && \
    .venv/bin/pip install pika pdf2image pytesseract minio elasticsearch

ENV PATH="/app/.venv/bin:$PATH"

CMD ["python", "ocrworker.py"]
