package com.abaduna.microservicioEventos.impServices;

import com.abaduna.microservicioEventos.DTO.ValidationResponse;
import com.abaduna.microservicioEventos.Services.IReservaService;
import com.abaduna.microservicioEventos.models.EstadoReserva;
import com.abaduna.microservicioEventos.models.Evento;
import com.abaduna.microservicioEventos.models.Reserva;
import com.abaduna.microservicioEventos.models.Rol;
import com.abaduna.microservicioEventos.repository.EventoRepository;
import com.abaduna.microservicioEventos.repository.ReservaRepository;
import com.abaduna.microservicioEventos.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservaService implements IReservaService {

    @Autowired
    private ReservaRepository reservaRepository;


    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Reserva crearReserva(String token, Long eventoId, int cantidadAsistentes) {

        ValidationResponse response = ValidationUtils.validarToken(token);

        Rol rol = Rol.valueOf(response.getRol().toString());
        if (response.getError() != null || rol.equals(Rol.user)) {
            throw new RuntimeException("Usuario no válido");
        }


        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));


        Reserva reserva = new Reserva();
        reserva.setEvento(evento);
        reserva.setIdusuario(response.getUserId().toString());
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setCantidadAsistentes(cantidadAsistentes);
        reserva.setEstado(EstadoReserva.PENDIENTE);


        return reservaRepository.save(reserva);
    }

    @Override
    public List<Reserva> obtenerTodasReservas(String token) {
        ValidationResponse response = ValidationUtils.validarToken(token);
        if (response.getError() != null) {
            throw new RuntimeException("Usuario no válido");
        }
        return reservaRepository.findAll();
    }
    @Override
    public Reserva obtenerReservaPorId(String token, Long id) {
        ValidationResponse response = ValidationUtils.validarToken(token);
        if (response.getError() != null) {
            throw new RuntimeException("Usuario no válido");
        }
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        if (!reserva.getIdusuario().equals(response.getUserId().toString())) {
            throw new RuntimeException("No autorizado para acceder a esta reserva");
        }
        return reserva;
    }
    @Override
    public Reserva actualizarReserva(String token, Long id, Reserva reservaActualizada) {
        ValidationResponse response = ValidationUtils.validarToken(token);
        if (response.getError() != null  || response.getRol().equals(Rol.admin)) {
            throw new RuntimeException("Usuario no válido");
        }
        Reserva reservaExistente = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        if (!reservaExistente.getIdusuario().equals(response.getUserId().toString())) {
            throw new RuntimeException("No autorizado para modificar esta reserva");
        }
        reservaExistente.setCantidadAsistentes(reservaActualizada.getCantidadAsistentes());
        reservaExistente.setEstado(reservaActualizada.getEstado());
        return reservaRepository.save(reservaExistente);
    }
    @Override
    public void eliminarReserva(String token, Long id) {
        ValidationResponse response = ValidationUtils.validarToken(token);
        if (response.getError() != null || response.getRol().equals(Rol.admin)) {
            throw new RuntimeException("Usuario no válido");
        }
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));
        if (!reserva.getIdusuario().equals(response.getUserId().toString())) {
            throw new RuntimeException("No autorizado para eliminar esta reserva");
        }
        reservaRepository.deleteById(id);
    }
}
