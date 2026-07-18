package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @AllArgsConstructor @Builder
public class EnvioResponse {
    private Long idEnvio;
    private String direccion;
    private String distrito;
    private String referencia;
    private EstadoEnvioDto estadoEnvio;
    private String repartidor;
    private String fechaEnvio;
    private String fechaEntrega;
    private String createdAt;
}
