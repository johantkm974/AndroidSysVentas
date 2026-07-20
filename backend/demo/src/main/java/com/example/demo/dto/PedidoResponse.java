package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data @AllArgsConstructor @Builder
public class PedidoResponse {
    private Long idPedido;
    private String numeroPedido;
    private String cliente;
    private String estado;
    private BigDecimal total;
    private String observacion;
    private LocalDateTime createdAt;
    private List<DetallePedidoResponse> detalles;
    private Long idEnvio;
    private String estadoEnvio;
    private String repartidor;

    @Data @AllArgsConstructor @Builder
    public static class DetallePedidoResponse {
        private Long idDetalle;
        private String producto;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotal;
    }
}
