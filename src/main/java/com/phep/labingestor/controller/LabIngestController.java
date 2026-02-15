package com.phep.labingestor.controller;

import com.phep.labingestor.model.LabEvent;
import com.phep.labingestor.service.LabProducer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/labs")
public class LabIngestController {

    private final LabProducer producer;

    public LabIngestController(LabProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<Void> send(@Valid @RequestBody LabEvent event) {

        // Ensure idempotency key exists (generated at producer edge)
        String eventId = (event.eventId() == null || event.eventId().isBlank())
                ? UUID.randomUUID().toString()
                : event.eventId().trim();

        LabEvent normalized = new LabEvent(
                eventId,
                event.caseId(),
                event.patientFirstName(),
                event.patientLastName(),
                event.dob(),
                event.testCode(),
                event.resultValue(),
                event.resultStatus(),
                event.labName()
        );

        log.info("ingest.request eventId={} testCode={} status={} patientLast={} caseId={}",
                normalized.eventId(), normalized.testCode(), normalized.resultStatus(),
                normalized.patientLastName(), normalized.caseId());

        producer.send(normalized);

        log.info("ingest.accepted eventId={} caseId={} patientLast={}",
                normalized.eventId(), normalized.caseId(), normalized.patientLastName());

        return ResponseEntity.accepted().build();
    }

}
