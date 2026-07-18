package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @AllArgsConstructor @Builder
public class SeguimientoResponse {
    private Long idSeguimiento;
    private EstadoEnvioDto estadoEnvio;
    private String observacion;
    private String createdAt;
}
