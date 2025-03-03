package com.abaduna.microservicioEventos.controllers;
import com.abaduna.microservicioEventos.DTO.ReservaDto;
import com.abaduna.microservicioEventos.Services.IReservaService;
import com.abaduna.microservicioEventos.models.Reserva;
import com.abaduna.microservicioEventos.impServices.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private IReservaService reservaService;

    @PostMapping
    public ResponseEntity<Reserva> crearReserva(@RequestBody ReservaDto reserva) {
        Reserva nuevaReserva = reservaService.crearReserva(
                reserva.getToken(),
                reserva.getIdEvento(),
                reserva.getCantidadAsistentes()

        );
        return ResponseEntity.ok(nuevaReserva);
    }
    @GetMapping
    public ResponseEntity<List<Reserva>> obtenerTodasReservas(@RequestHeader("Authorization") String token) {
        List<Reserva> reservas = reservaService.obtenerTodasReservas(token);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerReservaPorId(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            Reserva reserva = reservaService.obtenerReservaPorId(token, id);
            return ResponseEntity.ok(reserva);
        } catch (RuntimeException e) {
            return handleException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarReserva(@RequestHeader("Authorization") String token, @PathVariable Long id, @RequestBody Reserva reservaActualizada) {
        try {
            Reserva reserva = reservaService.actualizarReserva(token, id, reservaActualizada);
            return ResponseEntity.ok(reserva);
        } catch (RuntimeException e) {
            return handleException(e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarReserva(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        try {
            reservaService.eliminarReserva(token, id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return handleException(e);
        }
    }
    private ResponseEntity<String> handleException(RuntimeException e) {
        if (e.getMessage().contains("Usuario no válido")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } else if (e.getMessage().contains("No autorizado")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } else if (e.getMessage().contains("no encontrada")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
