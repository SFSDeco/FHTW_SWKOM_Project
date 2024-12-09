FROM python:3.9-slim

WORKDIR /app

COPY . /app

RUN python -m venv .venv && \
    .venv/bin/pip install --upgrade pip && \
    .venv/bin/pip install pika

ENV PATH="/app/.venv/bin:$PATH"

CMD ["python", "ocrworker.py"]
