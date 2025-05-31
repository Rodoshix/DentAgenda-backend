package com.dentagenda.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecuperarPasswordDTO {
    @NotBlank
    private String rut;
}