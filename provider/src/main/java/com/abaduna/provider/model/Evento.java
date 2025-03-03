package com.abaduna.provider.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Evento {


    private Long id;
    private String nombre;
    private LocalDateTime fechaEvento;
    private int capacidadMaxima;
}
