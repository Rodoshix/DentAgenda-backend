package com.dentagenda.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CrearUsuarioDTO {
    @NotBlank
    private String rut;

    @NotBlank
    private String password;

    @NotBlank
    private String rol;  // Debe ser "ODONTOLOGO" o "RECEPCIONISTA"
}