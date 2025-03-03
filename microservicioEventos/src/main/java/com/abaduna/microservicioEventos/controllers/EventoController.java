package com.abaduna.microservicioEventos.controllers;

import com.abaduna.microservicioEventos.DTO.EventDto;
import com.abaduna.microservicioEventos.Services.iEventoService;
import com.abaduna.microservicioEventos.models.Evento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    @Autowired
    private  iEventoService eventoService;



    @GetMapping
    public List<Evento> getAllEventos() {
        return eventoService.getAllEventos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> getEventoById(@PathVariable Long id) {
        return eventoService.getEventoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Evento> createEvento(@RequestBody EventDto evento) {
        return ResponseEntity.ok(eventoService.createEvento(evento));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> updateEvento(@PathVariable Long id, @RequestBody EventDto evento) {
        try {
            return ResponseEntity.ok(eventoService.updateEvento(id, evento));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvento(@PathVariable Long id,@RequestBody String token) {
        eventoService.deleteEvento(token,id);
        return ResponseEntity.noContent().build();
    }
}
