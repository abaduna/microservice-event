package com.abaduna.microservicioEventos.DTO;

import com.abaduna.microservicioEventos.models.EstadoReserva;
import com.abaduna.microservicioEventos.models.Evento;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservaDto {

    private Long id;



    private String token;


    private Long idEvento;


    private LocalDateTime fechaReserva;


    private int cantidadAsistentes;


    private EstadoReserva estado;
}
