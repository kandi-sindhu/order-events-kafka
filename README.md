# order-events-kafka

Two Spring Boot microservices that talk to each other through **Apache Kafka** instead of direct REST calls.

```
 client ──POST /api/orders──▶ order-service ──(orders.created)──▶ Kafka ──▶ notification-service
                                 producer                                      consumer
                                                                                  │ fails 3x
                                                                                  ▼
                                                                          orders.created-dlt
```

## What it shows

- **Event-driven design**: the order service publishes an `OrderCreatedEvent` and returns `202 Accepted`. It does not know who consumes the event.
- **Ordering per customer**: messages are keyed by `customerId`, so each customer's events stay in order on one partition.
- **Reliable producer**: `acks=all` with idempotence turned on.
- **Resilient consumer**: `ErrorHandlingDeserializer` plus a `DefaultErrorHandler` that retries 3 times and then sends the record to a **dead-letter topic**.
- **Bean validation** on the REST input.
- **Local Kafka in KRaft mode** (no ZooKeeper) through Docker Compose.

## Tech stack

Java 21 · Spring Boot 3 · Spring for Apache Kafka · Apache Kafka 3.8 (KRaft) · Docker Compose · Maven

## Run it

```bash
# 1. Start Kafka
docker compose up -d

# 2. Start the consumer
cd notification-service && mvn spring-boot:run

# 3. Start the producer (new terminal)
cd order-service && mvn spring-boot:run

# 4. Place an order
curl -X POST localhost:8081/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customerId":"C-100","product":"Brake Pads","quantity":2,"unitPrice":49.99}'
```

The notification-service log shows the event with its partition and offset.

## Project layout

```
order-service/          REST API + Kafka producer (port 8081)
notification-service/   Kafka consumer + DLT error handling (port 8082)
docker-compose.yml      Single-node Kafka broker
```

## Next steps

- Add a Schema Registry with Avro schemas
- Add the transactional outbox pattern for exactly-once publishing from a database
- Add Testcontainers integration tests
