package com.abaduna.microservicioEventos.Services;

import com.abaduna.microservicioEventos.DTO.EventDto;
import com.abaduna.microservicioEventos.models.Evento;

import java.util.List;
import java.util.Optional;

public interface iEventoService {
    List<Evento> getAllEventos();
    Optional<Evento> getEventoById(Long id);
    Evento createEvento(EventDto evento);
    Evento updateEvento(Long id, EventDto evento);
    void deleteEvento(String token,Long id);
}
