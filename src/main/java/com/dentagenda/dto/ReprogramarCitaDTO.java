package com.dentagenda.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReprogramarCitaDTO {

    @NotNull(message = "Debe indicar la nueva fecha y hora")
    @Future(message = "La nueva fecha debe ser futura")
    private LocalDateTime nuevaFechaHora;
}