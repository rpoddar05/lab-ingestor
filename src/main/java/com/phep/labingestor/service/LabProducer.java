package com.phep.labingestor.service;

import com.phep.labingestor.model.LabEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LabProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;
    public LabProducer(KafkaTemplate<String, Object> kafkaTemplate,
                       @Value("${app.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void send(LabEvent event) {
        //use last name or caseId as the key so all messages for a person go to the same partition
        String key = event.caseId() != null ? event.caseId() : event.patientLastName();

        kafkaTemplate.send(topic, key, event);
    }

}
