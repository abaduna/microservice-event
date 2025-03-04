package com.abaduna.provider.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;

@Configuration
public class KafkaAdminConfig {
    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    public KafkaAdmin kafkaAdmin(){
        var config = new HashMap<String,Object>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        config.put(AdminClientConfig.RECONNECT_BACKOFF_MS_CONFIG, "1000");
        config.put(AdminClientConfig.RECONNECT_BACKOFF_MAX_MS_CONFIG, "5000");
        config.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "30000");
        return new KafkaAdmin(config);
    }
    @Bean
    public KafkaAdmin.NewTopics topics(){
        return new KafkaAdmin.NewTopics(
            TopicBuilder.name("str-topic").partitions(2).replicas(1).build(),
            TopicBuilder.name("evento-request").partitions(2).replicas(1).build(),
            TopicBuilder.name("evento-reply").partitions(2).replicas(1).build()
        );
    }
}