package com.abaduna.microservicioEventos.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class EventDto {

    private Long id;
    private String token;
    private String nombre;
    private LocalDateTime fechaEvento;
    private int capacidadMaxima;
}
