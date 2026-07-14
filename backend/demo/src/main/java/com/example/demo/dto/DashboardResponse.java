package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data @AllArgsConstructor @Builder
public class DashboardResponse {
    private ResumenGeneral resumen;
    private List<ProductoTop> productosMasVendidos;
    private List<ProductoStockBajo> productosStockBajo;

    @Data @AllArgsConstructor @Builder
    public static class ResumenGeneral {
        private long totalProductos;
        private long totalUsuarios;
        private long totalPedidosPendientes;
        private long totalVentasDelMes;
        private BigDecimal ingresosDelMes;
        private long totalEnviosPendientes;
    }

    @Data @AllArgsConstructor @Builder
    public static class ProductoTop {
        private String nombre;
        private long totalVendido;
    }

    @Data @AllArgsConstructor @Builder
    public static class ProductoStockBajo {
        private Long idProducto;
        private String nombre;
        private String codigo;
        private int stock;
        private int stockMinimo;
    }
}
