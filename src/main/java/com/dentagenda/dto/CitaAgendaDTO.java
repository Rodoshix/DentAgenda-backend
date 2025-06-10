package com.dentagenda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CitaAgendaDTO {
    private Long id;
    private String hora;              // solo la hora formateada (ej: "09:00")
    private String pacienteNombre;
    private String pacienteRut;
    private String motivo;
    private String estado;
    private boolean tratamientoRegistrado;

}