package com.dentagenda.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AgendarCitaDTO {

    @NotNull(message = "Debe indicar la fecha y hora de la cita")
    @Future(message = "La cita debe ser en el futuro")
    private LocalDateTime fechaHora;

    @NotNull(message = "Debe especificar el ID del paciente")
    private Long pacienteId;

    @NotNull(message = "Debe especificar el ID del odontólogo")
    private Long odontologoId;

    private String motivo; // opcional
}