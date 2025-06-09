package com.dentagenda.dto;

import lombok.Data;

@Data
public class CitaDTO {
    private Long pacienteId;
    private Long odontologoId;
    private String fechaHora;
    private String motivo;
}
