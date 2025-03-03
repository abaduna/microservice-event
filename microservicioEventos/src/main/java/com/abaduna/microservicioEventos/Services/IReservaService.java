package com.abaduna.microservicioEventos.Services;

import com.abaduna.microservicioEventos.models.Evento;
import com.abaduna.microservicioEventos.models.Reserva;

import java.util.List;

// com.abaduna.microservicioEventos.services.IReservaService
public interface IReservaService {
    Reserva crearReserva(String token, Long eventoId, int cantidadAsistentes);
    List<Reserva> obtenerTodasReservas(String token);
    Reserva obtenerReservaPorId(String token, Long id);
    Reserva actualizarReserva(String token, Long id, Reserva reservaActualizada);
    void eliminarReserva(String token, Long id);
}