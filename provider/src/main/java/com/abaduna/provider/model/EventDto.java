package com.abaduna.provider.model;
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
