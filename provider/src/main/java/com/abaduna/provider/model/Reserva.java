package com.abaduna.provider.model;


import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Reserva {


    private Long id;



    private String idusuario;


    private Evento evento;


    private LocalDateTime fechaReserva;


    private int cantidadAsistentes;



    private EstadoReserva estado;


}