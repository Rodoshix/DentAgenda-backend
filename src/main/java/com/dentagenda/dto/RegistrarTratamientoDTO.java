package com.dentagenda.dto;

import lombok.Data;

@Data
public class RegistrarTratamientoDTO {
    private String diagnostico;
    private String procedimiento;
    private Long idCita;
}