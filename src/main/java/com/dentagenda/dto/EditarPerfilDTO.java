package com.dentagenda.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EditarPerfilDTO {

    @NotBlank
    private String nombre;

    @NotBlank
    private String telefono;

    @Email
    private String correo;
}