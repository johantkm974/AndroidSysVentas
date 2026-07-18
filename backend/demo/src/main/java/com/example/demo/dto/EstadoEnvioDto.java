package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @AllArgsConstructor @Builder
public class EstadoEnvioDto {
    private Long idEstadoEnvio;
    private String nombre;
    private String descripcion;
}
