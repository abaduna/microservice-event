package com.abaduna.microservicioEventos.repository;

import com.abaduna.microservicioEventos.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
}
