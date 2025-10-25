FROM python:3.11-slim
WORKDIR /app

# install system dependencies needed for psycopg2
RUN apt-get update \
    && apt-get install -y --no-install-recommends gcc libpq-dev \
    && rm -rf /var/lib/apt/lists/*

COPY scripts/requirements.txt /app/requirements.txt
RUN pip install --no-cache-dir -r /app/requirements.txt

# copy repository (only what we need is fine)
COPY . /app

# default envs can be overridden by docker-compose or CLI
ENV BACKEND_URL=http://localhost:8080/api \
    PG_HOST=localhost \
    PG_PORT=5432 \
    PG_USER=postgres \
    PG_PASS=postgres \
    PG_DB=gislib

CMD ["python3", "scripts/test_backend.py"]
