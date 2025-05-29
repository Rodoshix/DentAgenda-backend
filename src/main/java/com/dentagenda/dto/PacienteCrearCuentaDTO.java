package com.dentagenda.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PacienteCrearCuentaDTO {

    @NotBlank(message = "El RUT es obligatorio")
    private String rut;

    @NotBlank(message = "La contraseña es obligatoria")
    private String contrasena;

    // Estos campos son opcionales, solo se usan si el paciente nunca ha sido registrado
    private String nombre;
    private String correo;
    private String telefono;
}