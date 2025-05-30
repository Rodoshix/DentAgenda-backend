package com.dentagenda.dto;

import lombok.Data;

@Data
public class RegistroRecepcionistaDTO {
    private String nombre;
    private String rut;
    private String correo;
    private String telefono;
    private String password;  // Contraseña para crear la cuenta
}