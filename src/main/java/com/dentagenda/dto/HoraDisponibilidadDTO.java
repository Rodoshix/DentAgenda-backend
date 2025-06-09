package com.dentagenda.dto;

import lombok.Data;

@Data
public class HoraDisponibilidadDTO {
    private String hora;
    private String paciente; // null si está libre
}
