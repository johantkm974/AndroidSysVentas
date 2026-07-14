package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnvioRequest {
    @NotNull
    private Long idPedido;
    @NotBlank
    private String direccion;
    @NotBlank
    private String distrito;
    private String referencia;
}
