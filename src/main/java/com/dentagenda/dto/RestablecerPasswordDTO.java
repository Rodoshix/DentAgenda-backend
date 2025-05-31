package com.dentagenda.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RestablecerPasswordDTO {
    @NotBlank
    private String token;

    @NotBlank
    private String nuevaPassword;
}
