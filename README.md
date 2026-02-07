# Lab Ingestor Service

The **lab-ingestor** service receives lab results via HTTP and publishes them to Kafka
for downstream processing by the case-service.

This service is intentionally thin and focused on:
- Validation
- Normalization
- Reliable event publishing

---

## Responsibilities

- Accept lab result submissions via REST
- Attach correlation IDs for traceability
- Publish normalized `LabEvent` messages to Kafka
- Ensure ordering and partition affinity per patient / case

---

## Kafka Publishing Strategy

### Key Selection (Partitioning)

A dedicated `KafkaKeyStrategy` determines the Kafka message key:

1. **If `caseId` is present**
    - Use `caseId` as the Kafka key
    - Guarantees ordering for a specific case
    - Enables replay and internal republishing scenarios

2. **If `caseId` is absent**
    - Use a hashed patient identity (e.g. last name / DOB)
    - Ensures labs for the same patient land in the same partition

This design balances:
- Ordering guarantees
- Safe parallelism
- Forward compatibility with replay jobs

---

## Correlation IDs

- Correlation IDs are generated or propagated via `X-Correlation-Id`
- Added to Kafka headers
- Preserved across producer → consumer boundary
- Enables end-to-end traceability in logs

---

## Failure Handling (Current)

- Producer logs success/failure with metadata
- Consumer-side failures are handled downstream (case-service)

Future improvements:
- DLQ topic
- Publish failures to a persistent error store
- Alerting integration

---

## Lab Event Contract

The following JSON represents the event published to Kafka by this service.

- `caseId` is optional
- When absent, downstream services perform patient matching
- When present, it is treated as a hint for fast-path case attachment

### Example Event Payload

```json
{
  "caseId": null,
  "patientFirstName": "Aman",
  "patientLastName": "Kedia",
  "dob": "2020-06-01",
  "testCode": "GONORREHA",
  "resultValue": "Detected",
  "resultStatus": "POSITIVE",
  "labName": "QUEST DIAGNOSTIC"
}
```
### Local Development
docker-compose up 
./mvnw spring-boot:run