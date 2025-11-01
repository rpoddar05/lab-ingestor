package com.phep.labingestor.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    NewTopic labEvents(@Value("${app.topic}") String topic) {
        //1 partition + replication-factor 1 is gine for local dev
        return new NewTopic("lab-events", 1, (short) 1);
    }

}
