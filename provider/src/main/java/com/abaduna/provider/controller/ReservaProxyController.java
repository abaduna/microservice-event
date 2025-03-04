package com.abaduna.provider.controller;

import com.abaduna.provider.kafka.KafkaProducerService;
import com.abaduna.provider.model.Reserva;
import com.abaduna.provider.model.ReservaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/proxy/reservas")
public class ReservaProxyController {

    private final RestTemplate restTemplate;
    private final String BASE_URL = "http://localhost:8080/reservas"; // Cambia la URL según la ubicación de la API de reservas

    private final KafkaProducerService kafkaProducerService;
    private final ObjectMapper objectMapper; // Para convertir a JSON

    @Autowired
    public ReservaProxyController(RestTemplate restTemplate, KafkaProducerService kafkaProducerService, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.kafkaProducerService = kafkaProducerService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<Reserva> crearReserva(@RequestBody ReservaDto reserva) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", reserva.getToken()); // Se asume que el token viene en el DTO

            HttpEntity<ReservaDto> request = new HttpEntity<>(reserva, headers);
            ResponseEntity<Reserva> response = restTemplate.exchange(
                    BASE_URL,
                    HttpMethod.POST,
                    request,
                    Reserva.class
            );

            // Convertir el objeto ReservaDto a JSON
            String reservaJson = objectMapper.writeValueAsString(reserva);

            // Enviar el mensaje serializado a Kafka
            kafkaProducerService.sendEventoToMultipleTopics(reservaJson, List.of("reservas_creadas_topic", "reservas_actualizadas_topic"));

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<List<Reserva>> obtenerTodasReservas(@RequestHeader("Authorization") String token) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<Reserva[]> response = restTemplate.exchange(
                    BASE_URL,
                    HttpMethod.GET,
                    request,
                    Reserva[].class
            );

            List<Reserva> reservas = Arrays.asList(response.getBody());
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reserva> obtenerReservaPorId(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<Reserva> response = restTemplate.exchange(
                    BASE_URL + "/" + id,
                    HttpMethod.GET,
                    request,
                    Reserva.class
            );

            // Convertir el objeto Reserva a JSON
            String reservaJson = objectMapper.writeValueAsString(response.getBody());

            // Enviar el mensaje serializado a Kafka
            kafkaProducerService.sendEventoToMultipleTopics(reservaJson, List.of("reservas_obtenidas_topic", "reservas_actualizadas_topic"));

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reserva> actualizarReserva(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Reserva reservaActualizada) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            HttpEntity<Reserva> request = new HttpEntity<>(reservaActualizada, headers);
            ResponseEntity<Reserva> response = restTemplate.exchange(
                    BASE_URL + "/" + id,
                    HttpMethod.PUT,
                    request,
                    Reserva.class
            );

            // Convertir el objeto actualizado a JSON
            String reservaJson = objectMapper.writeValueAsString(reservaActualizada);

            // Enviar el mensaje serializado a Kafka
            kafkaProducerService.sendEventoToMultipleTopics(reservaJson, List.of("reservas_actualizadas_topic"));

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReserva(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", token);

            HttpEntity<String> request = new HttpEntity<>(headers);
            restTemplate.exchange(
                    BASE_URL + "/" + id,
                    HttpMethod.DELETE,
                    request,
                    Void.class
            );

            // Enviar el ID serializado a Kafka
            String idJson = objectMapper.writeValueAsString(id);
            kafkaProducerService.sendEventoToMultipleTopics(idJson, List.of("reservas_eliminadas_topic"));

            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
