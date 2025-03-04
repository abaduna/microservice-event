package com.abaduna.microservicioEventos.service;

import com.abaduna.microservicioEventos.Services.iEventoService;
import com.abaduna.microservicioEventos.models.Evento;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class EventoConsumerService {
    @Autowired
    private KafkaTemplate<String, List<Evento>> kafkaTemplate;
     
    @Autowired
    private iEventoService eventoService;

    @KafkaListener(
        topics = "evento-request",
        groupId = "evento-group",
        containerFactory = "eventoListenerContainerFactory"
    )
    public void handleRequest(ConsumerRecord<String, Evento> record) {
        try {
            log.info("Recibida solicitud de eventos");

            String correlationId = new String(record.headers().lastHeader("correlationId").value());
            
            // Cambiamos para usar un topic fijo para respuestas
            String replyTo = "evento-replies";

            // Obtener los eventos usando el servicio
            List<Evento> eventos = eventoService.getAllEventos();
            log.info("Respuesta eventos: {}", eventos);
            // Crear registro de respuesta
            ProducerRecord<String, List<Evento>> replyRecord =
                    new ProducerRecord<>(replyTo, eventos);
            
            // Importante: incluir el correlationId en la respuesta
            replyRecord.headers().add("correlationId", correlationId.getBytes());

            // Enviar respuesta
            kafkaTemplate.send(replyRecord);
            log.info("Respuesta enviada para correlationId: {}", correlationId);
        } catch (Exception e) {
            log.error("Error procesando solicitud de eventos", e);
        }
    }
}