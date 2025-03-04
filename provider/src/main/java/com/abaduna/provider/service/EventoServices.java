package com.abaduna.provider.service;

import com.abaduna.provider.model.Evento;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventoServices {
    private static final Logger logger = LoggerFactory.getLogger(EventoServices.class);

    @Autowired
    private KafkaTemplate<String, Evento> kafkaTemplate;

    @Autowired
    private KafkaProperties kafkaProperties;

    private static final String REQUEST_TOPIC = "evento-request";
    private static final String REPLY_TOPIC = "evento-replies";
    private final ConcurrentHashMap<String, CompletableFuture<List<Evento>>> replyFutures = new ConcurrentHashMap<>();

    public List<Evento> getAllEventos() {
        logger.info("Sending request to get all eventos");
        String correlationId = UUID.randomUUID().toString();
        Evento requestEvento = new Evento();

        ProducerRecord<String, Evento> record = new ProducerRecord<>(REQUEST_TOPIC, null, requestEvento);
        record.headers().add("correlationId", correlationId.getBytes());
        record.headers().add("replyTo", REPLY_TOPIC.getBytes());
        record.headers().add("operation", "getAllEventos".getBytes());

        CompletableFuture<List<Evento>> future = new CompletableFuture<>();
        replyFutures.put(correlationId, future);

        kafkaTemplate.send(record);
        logger.info("Sent request to Kafka with correlationId: {}", correlationId);

        try {
            return future.get(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            replyFutures.remove(correlationId);
            logger.error("Error while waiting for response", e);
            throw new RuntimeException("Error al obtener eventos", e);
        }
    }

    @PostConstruct
    public void setupReplyListener() {
        logger.info("Setting up Kafka reply listener");
        ConsumerFactory<String, List<Evento>> consumerFactory = new DefaultKafkaConsumerFactory<>(
            kafkaProperties.buildConsumerProperties(),
            new StringDeserializer(),
            new JsonDeserializer<>(new TypeReference<List<Evento>>() {})
        );

        ContainerProperties containerProperties = new ContainerProperties(REPLY_TOPIC);
        KafkaMessageListenerContainer<String, List<Evento>> container = 
            new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

        container.setupMessageListener((MessageListener<String, List<Evento>>) record -> {
            try {
                String correlationId = new String(record.headers().lastHeader("correlationId").value());
                logger.info("Received message with correlationId: {}", correlationId);
                logger.info("Message value: {}", record.value());
                logger.info("Current replyFutures keys: {}", replyFutures.keySet());
                CompletableFuture<List<Evento>> future = replyFutures.remove(correlationId);
                if (future != null) {
                    future.complete(record.value());
                    logger.info("Completed future for correlationId: {}", correlationId);
                } else {
                    logger.warn("No future found for correlationId: {}", correlationId);
                }
            } catch (Exception e) {
                logger.error("Error processing received message", e);
            }
        });

        container.start();
        logger.info("Kafka reply listener started");
    }
}
