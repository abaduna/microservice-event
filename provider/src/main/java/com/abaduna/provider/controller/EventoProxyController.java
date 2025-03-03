package com.abaduna.provider.controller;

import com.abaduna.provider.model.EventDto;
import com.abaduna.provider.model.Evento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/proxy/eventos")
public class EventoProxyController {

    private final RestTemplate restTemplate;
    private final String BASE_URL = "http://localhost:8080/api/eventos"; // Cambia la URL según la ubicación del otro servicio

    @Autowired
    public EventoProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping
    public ResponseEntity<List<Evento>> getAllEventos() {
        ResponseEntity<Evento[]> response = restTemplate.getForEntity(BASE_URL, Evento[].class);
        List<Evento> eventos = Arrays.asList(response.getBody());
        return ResponseEntity.ok(eventos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> getEventoById(@PathVariable Long id) {
        try {
            Evento evento = restTemplate.getForObject(BASE_URL + "/" + id, Evento.class);
            return ResponseEntity.ok(evento);
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Evento> createEvento(@RequestBody EventDto evento) {
        ResponseEntity<Evento> response = restTemplate.postForEntity(BASE_URL, evento, Evento.class);
        return ResponseEntity.ok(response.getBody());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> updateEvento(@PathVariable Long id, @RequestBody EventDto evento) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<EventDto> request = new HttpEntity<>(evento, headers);

            ResponseEntity<Evento> response = restTemplate.exchange(
                    BASE_URL + "/" + id,
                    HttpMethod.PUT,
                    request,
                    Evento.class
            );

            return ResponseEntity.ok(response.getBody());
        } catch (HttpClientErrorException.NotFound e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvento(@PathVariable Long id, @RequestBody String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(token, headers);

        restTemplate.exchange(BASE_URL + "/" + id, HttpMethod.DELETE, request, Void.class);
        return ResponseEntity.noContent().build();
    }
}
