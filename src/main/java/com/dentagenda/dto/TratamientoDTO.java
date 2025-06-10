package com.dentagenda.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TratamientoDTO {
    private Long id;
    private String fecha;
    private String diagnostico;
    private String procedimiento;
    private String observacion;
    private String pacienteNombre;
}