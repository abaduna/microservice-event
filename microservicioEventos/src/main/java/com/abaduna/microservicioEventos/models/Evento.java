package com.abaduna.microservicioEventos.models;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;



import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "eventos")
public class Evento implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private LocalDateTime fechaEvento;

    @Column(nullable = false)
    private int capacidadMaxima;

    // Constructor vacío necesario para la serialización
    public Evento() {}

    // Constructor con todos los campos
    public Evento(Long id, String nombre, LocalDateTime fechaEvento, int capacidadMaxima) {
        this.id = id;
        this.nombre = nombre;
        this.fechaEvento = fechaEvento;
        this.capacidadMaxima = capacidadMaxima;
    }
}