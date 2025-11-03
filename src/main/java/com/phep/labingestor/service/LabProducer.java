package com.phep.labingestor.service;

import com.phep.labingestor.model.LabEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
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

        log.debug("publishing.lab key={} topic={} testCode={} status={}",
                key, topic, event.testCode(), event.resultStatus());

        kafkaTemplate.send(topic, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("publishing.lab FAILED key={} topic={} error={}",
                                key, topic, ex.toString(), ex);
                    }else{
                        var md = result.getRecordMetadata();
                        log.info("publishing.lab OK key={} topic={} partition={} offset={}",
                                key, topic, md.partition(), md.offset());
                    }
                });
    }

}
