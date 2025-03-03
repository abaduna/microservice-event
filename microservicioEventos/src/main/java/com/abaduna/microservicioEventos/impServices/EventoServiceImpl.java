package com.abaduna.microservicioEventos.impServices;

import com.abaduna.microservicioEventos.models.Rol;
import com.abaduna.microservicioEventos.DTO.EventDto;
import com.abaduna.microservicioEventos.DTO.ValidationResponse;
import com.abaduna.microservicioEventos.Services.iEventoService;
import com.abaduna.microservicioEventos.models.Evento;
import com.abaduna.microservicioEventos.repository.EventoRepository;

import com.abaduna.microservicioEventos.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EventoServiceImpl implements iEventoService {


    @Autowired
    private  EventoRepository eventoRepository;



    @Override
    public List<Evento> getAllEventos() {
        return eventoRepository.findAll();
    }

    @Override
    public Optional<Evento> getEventoById(Long id) {
        return eventoRepository.findById(id);
    }

    @Override
    public Evento createEvento(EventDto evento) {
        ValidationResponse response = ValidationUtils.validarToken(evento.getToken());
        if (response.getError() != null || response.getRol().equals(Rol.user)) {
            throw new RuntimeException("Usuario no válido");
        }
        Evento createEvento= new Evento();
        createEvento.setFechaEvento(evento.getFechaEvento());
        createEvento.setCapacidadMaxima(evento.getCapacidadMaxima());
        createEvento.setNombre(evento.getNombre());

        return eventoRepository.save(createEvento);
    }

    @Override
    public Evento updateEvento(Long id, EventDto evento) {
        ValidationResponse response = ValidationUtils.validarToken(evento.getToken());
        if (response.getError() != null || response.getRol().equals(Rol.admin)) {
            throw new RuntimeException("Usuario no válido");
        }
        return eventoRepository.findById(id)
                .map(existingEvento -> {
                    existingEvento.setNombre(evento.getNombre());
                    existingEvento.setFechaEvento(evento.getFechaEvento());
                    existingEvento.setCapacidadMaxima(evento.getCapacidadMaxima());
                    return eventoRepository.save(existingEvento);
                })
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
    }

    @Override
    public void deleteEvento(String token,Long id) {
        ValidationResponse response = ValidationUtils.validarToken(token);
        if (response.getError() != null || response.getRol().equals(Rol.admin)) {
            throw new RuntimeException("Usuario no válido");
        }
        eventoRepository.deleteById(id);
    }
}
