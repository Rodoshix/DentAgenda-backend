package com.dentagenda.dto;

import lombok.Data;
import java.util.List;

@Data
public class OdontologoDisponibilidadDTO {
    private Long odontologoId;
    private String nombre;
    private List<String> horasDisponibles;
}