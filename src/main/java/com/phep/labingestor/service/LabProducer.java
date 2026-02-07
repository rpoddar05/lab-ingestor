package com.phep.labingestor.service;

import com.phep.labingestor.config.CorrelationIdFilter;
import com.phep.labingestor.model.LabEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class LabProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String topic;
    private final KafkaKeyStrategy keyStrategy;
    public LabProducer(KafkaTemplate<String, Object> kafkaTemplate,KafkaKeyStrategy keyStrategy,
                       @Value("${app.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.keyStrategy = keyStrategy;
    }

    public void send(LabEvent event) {

        // Key strategy: if caseId present -> use it, else hash patient identity
        String key = keyStrategy.keyFor(event);
        // Fetch correlation id from MDC (set by CorrelationIdFilter)
        String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);

        // Create ProducerRecord so we can attach headers
        ProducerRecord<String, Object> record =
                new ProducerRecord<>(topic, key, event);

        // Add correlation id to Kafka headers
        if(correlationId != null){
            record.headers().add(CorrelationIdFilter.HEADER,
                    correlationId.getBytes(StandardCharsets.UTF_8));
        }

        log.debug("publishing.lab key={} topic={} corrId={} testCode={} status={}",
                key, topic, correlationId, event.testCode(), event.resultStatus());


        kafkaTemplate.send(record)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("publishing.lab FAILED key={} topic={} corrId={} error={}",
                                key, topic, correlationId, ex.toString(), ex);
                    }else{
                        var md = result.getRecordMetadata();
                        log.info("publishing.lab OK key={} topic={} corrId={} partition={} offset={}",
                                key, topic, correlationId, md.partition(), md.offset());
                    }
                });
    }

}
