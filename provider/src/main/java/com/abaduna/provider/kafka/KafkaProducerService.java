package com.abaduna.provider.kafka;



import com.abaduna.provider.model.EventDto;
import com.abaduna.provider.model.Evento;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    // Método para enviar el evento a múltiples tópicos
    public void sendEventoToMultipleTopics(EventDto eventDto, List<String> topics) {
        try {
            // Convertir el EventDto a JSON
            String eventDtoJson = objectMapper.writeValueAsString(eventDto);

            // Enviar el evento convertido a JSON a cada tópico
            for (String topic : topics) {
                kafkaTemplate.send(topic, eventDtoJson);
                System.out.println("✅ Evento enviado a Kafka en el tópico: " + topic + " - " + eventDtoJson);
            }
        } catch (JsonProcessingException e) {
            System.err.println("Error al convertir el evento a JSON: " + e.getMessage());
        }
    }
    public void sendEventoToMultipleTopicsSendID(Long eventId, List<String> topics) {
        try {
            String eventIdJson = objectMapper.writeValueAsString(eventId); // Convertir el id a JSON
            for (String topic : topics) {
                kafkaTemplate.send(topic, eventIdJson); // Enviar el id del evento a cada tópico
                System.out.println("✅ Evento ID enviado a Kafka: " + eventIdJson + " al tópico: " + topic);
            }
        } catch (JsonProcessingException e) {
            System.err.println("Error al convertir el ID del evento a JSON: " + e.getMessage());
        }
    }
    public void sendTopic(List<String> topics, Evento evento) throws JsonProcessingException {

        for (String topic : topics) {
            // Serializa el evento antes de enviarlo
            String eventoJson = objectMapper.writeValueAsString(evento);
            kafkaTemplate.send(topic, eventoJson);
            System.out.println("✅ Evento enviado a Kafka en el tópico: " + topic + " - Evento: " + evento);
        }
    }
    // Método para enviar un mensaje a un tópico específico
    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
        System.out.println("Mensaje enviado al tópico: " + topic + " - Mensaje: " + message);
    }

    // Método para enviar eventos a múltiples tópicos
    public void sendEventoToMultipleTopics(String message, List<String> topics) {
        for (String topic : topics) {
            sendMessage(topic, message); // Enviar el mensaje a cada tópico
        }
    }

    // Método para enviar un mensaje con un ID a múltiples tópicos (por ejemplo, para eliminar o obtener por ID)
    public void sendEventoToMultipleTopicsWithID(Long id, List<String> topics) {
        String message = "Evento con ID: " + id;
        for (String topic : topics) {
            sendMessage(topic, message); // Enviar el mensaje con ID a cada tópico
        }
    }
}
