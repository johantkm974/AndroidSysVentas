package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @AllArgsConstructor @Builder
public class VentaResponse {
    private Long idVenta;
    private String numeroVenta;
    private String numeroPedido;
    private String cliente;
    private String metodoPago;
    private BigDecimal subtotal;
    private BigDecimal igv;
    private BigDecimal total;
    private String estado;
    private LocalDateTime createdAt;
    private List<DetalleVentaResponse> detalles;

    @Data @AllArgsConstructor @Builder
    public static class DetalleVentaResponse {
        private String producto;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }
}
