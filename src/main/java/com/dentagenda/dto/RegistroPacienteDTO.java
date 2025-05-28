package com.dentagenda.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistroPacienteDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El RUT es obligatorio")
    private String rut;

    @Email(message = "Correo electrónico inválido")
    private String correo;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;
}