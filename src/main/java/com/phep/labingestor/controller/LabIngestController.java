package com.phep.labingestor.controller;

import com.phep.labingestor.model.LabEvent;
import com.phep.labingestor.service.LabProducer;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/labs")
public class LabIngestController {

    private final LabProducer producer;

    public LabIngestController(LabProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<Void> send(@Valid @RequestBody LabEvent event) {
            producer.send(event);
            return ResponseEntity.accepted().build();
    }

}
