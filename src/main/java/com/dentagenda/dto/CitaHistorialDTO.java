package com.dentagenda.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class CitaHistorialDTO {

    private Long id;
    private LocalDateTime fechaHora;
    private String estado;
    private String tratamiento;
    private String observacion;
    private String odontologoNombre;

}
