package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @AllArgsConstructor @Builder
public class ProductoResponse {
    private Long idProducto;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private Integer stock;
    private Integer stockMinimo;
    private String imagen;
    private String categoria;
    private String marca;
    private String proveedor;
    private Boolean activo;
    private LocalDateTime createdAt;
}
