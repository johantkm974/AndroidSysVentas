package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VentaRequest {
    @NotNull
    private Long idPedido;
    @NotNull
    private Long idMetodoPago;
}
