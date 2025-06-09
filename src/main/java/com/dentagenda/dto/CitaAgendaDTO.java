package com.dentagenda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CitaAgendaDTO {
    private Long id;
    private String hora;
    private String pacienteNombre;
    private String motivo;
}