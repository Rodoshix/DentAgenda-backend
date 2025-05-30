package com.dentagenda.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class BloqueoAgenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    private String motivo; // Opcional: vacaciones, enfermedad, etc.

    @ManyToOne
    @JoinColumn(name = "odontologo_id")
    private Odontologo odontologo;
}