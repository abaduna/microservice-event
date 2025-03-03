package com.abaduna.provider.model;

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
