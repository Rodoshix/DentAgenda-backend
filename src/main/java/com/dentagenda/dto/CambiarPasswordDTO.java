package com.dentagenda.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CambiarPasswordDTO {
    @NotBlank
    private String passwordActual;

    @NotBlank
    private String nuevaPassword;
}