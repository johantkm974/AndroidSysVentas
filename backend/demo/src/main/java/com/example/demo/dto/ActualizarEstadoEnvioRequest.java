package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarEstadoEnvioRequest {
    @NotNull
    private Long idEstadoEnvio;
    private String observacion;
}
