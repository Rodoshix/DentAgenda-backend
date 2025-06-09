package com.dentagenda.dto;

import com.dentagenda.model.EstadoCita;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CitaRecepcionDTO {
    private Long id;
    private LocalDateTime fechaHora;
    private String motivo;
    private EstadoCita estado;
    private String odontologoNombre;
}