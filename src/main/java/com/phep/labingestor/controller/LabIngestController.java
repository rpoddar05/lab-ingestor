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

        log.info("ingest.request testCode={} status={} patientLast={} caseId={}",
                event.testCode(), event.resultStatus(), event.patientLastName(), event.caseId());
        producer.send(event);
        log.info("ingest.accepted caseId={} patientLast={}", event.caseId(), event.patientLastName());
        return ResponseEntity.accepted().build();
    }

}
