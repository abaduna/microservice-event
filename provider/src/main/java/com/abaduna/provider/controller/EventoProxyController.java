package com.abaduna.provider.controller;

import com.abaduna.provider.model.EventDto;
import com.abaduna.provider.model.Evento;
import com.abaduna.provider.service.EventoServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/proxy/eventos")
public class EventoProxyController {

    private static final Logger logger = LoggerFactory.getLogger(EventoProxyController.class);

    private final RestTemplate restTemplate;
    private final String BASE_URL = "http://localhost:8080/api/eventos"; // Cambia la URL según la ubicación del otro servicio

    @Autowired
    public EventoProxyController(RestTemplate restTemplate, EventoServices eventoServices) {
        this.restTemplate = restTemplate;
        this.eventoServices = eventoServices;
    }

    private final EventoServices eventoServices;

    @GetMapping
    public ResponseEntity<List<Evento>> getAllEventos() {
        try {
            logger.info("Request to get all eventos");
            List<Evento> eventos = eventoServices.getAllEventos();
            logger.info("Received eventos: {}", eventos);
            return ResponseEntity.ok(eventos);
        } catch (RuntimeException e) {
            logger.error("Error getting eventos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> getEventoById(@PathVariable Long id) {
        try {
            logger.info("Request to get evento by id: {}", id);
            Evento evento = restTemplate.getForObject(BASE_URL + "/" + id, Evento.class);
            logger.info("Received evento: {}", evento);
            return ResponseEntity.ok(evento);
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Evento not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Evento> createEvento(@RequestBody EventDto evento) {
        logger.info("Request to create evento: {}", evento);
        ResponseEntity<Evento> response = restTemplate.postForEntity(BASE_URL, evento, Evento.class);
        logger.info("Created evento: {}", response.getBody());
        return ResponseEntity.ok(response.getBody());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> updateEvento(@PathVariable Long id, @RequestBody EventDto evento) {
        try {
            logger.info("Request to update evento with id: {}", id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<EventDto> request = new HttpEntity<>(evento, headers);

            ResponseEntity<Evento> response = restTemplate.exchange(
                    BASE_URL + "/" + id,
                    HttpMethod.PUT,
                    request,
                    Evento.class
            );

            logger.info("Updated evento: {}", response.getBody());
            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Evento not found with id: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvento(@PathVariable Long id, @RequestBody String token) {
        logger.info("Request to delete evento with id: {}", id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(token, headers);

        restTemplate.exchange(BASE_URL + "/" + id, HttpMethod.DELETE, request, Void.class);
        logger.info("Deleted evento with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
