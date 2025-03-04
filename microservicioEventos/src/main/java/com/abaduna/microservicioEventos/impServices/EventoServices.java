package com.abaduna.microservicioEventos.impServices;

import com.abaduna.microservicioEventos.models.Evento;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class EventoServices {
    @Autowired
    private KafkaTemplate<String, Evento> kafkaTemplate;
    
    private static final String REQUEST_TOPIC = "evento-request";
    private static final String REPLY_TOPIC = "evento-replies";
    
    // Mapa para almacenar los futures pendientes por correlationId
    private final Map<String, CompletableFuture<List<Evento>>> pendingRequests = new ConcurrentHashMap<>();

    public List<Evento> getAllEventos() {
        // Crear un correlationId único para identificar la solicitud
        String correlationId = UUID.randomUUID().toString();

        // Crear un future para esta solicitud
        CompletableFuture<List<Evento>> future = new CompletableFuture<>();
        pendingRequests.put(correlationId, future);

        try {
            // Configurar headers
            ProducerRecord<String, Evento> record = new ProducerRecord<>(REQUEST_TOPIC, null);
            record.headers().add("correlationId", correlationId.getBytes());
            record.headers().add("replyTo", REPLY_TOPIC.getBytes());

            // Enviar mensaje
            kafkaTemplate.send(record);

            // Esperar respuesta (timeout de 5 segundos)
            return future.get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener eventos", e);
        } finally {
            // Limpiar el future del mapa para evitar memory leaks
            pendingRequests.remove(correlationId);
        }
    }

    // Listener para recibir las respuestas
    @KafkaListener(
            topics = "evento-reply",
            groupId = "evento-service-group",
            containerFactory = "listEventoListenerContainerFactory"
    )
    public void handleReply(ConsumerRecord<String, List<Evento>> record) {
        String correlationId = new String(record.headers().lastHeader("correlationId").value());
        
        // Buscar el future correspondiente a este correlationId
        CompletableFuture<List<Evento>> future = pendingRequests.get(correlationId);
        
        if (future != null) {
            // Completar el future con la respuesta
            future.complete(record.value());
        }
    }
}