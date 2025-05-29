package com.dentagenda.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String rut;
    private String password;
}