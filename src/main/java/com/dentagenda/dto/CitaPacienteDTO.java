package com.dentagenda.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CitaPacienteDTO {
    private Long id;
    private LocalDateTime fechaHora;
    private String estado;
    
    private Long odontologoId;
    private String odontologoNombre;

    private String observacion;
}