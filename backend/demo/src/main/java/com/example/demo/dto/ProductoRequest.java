package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoRequest {
    @NotBlank
    private String codigo;
    @NotBlank
    private String nombre;
    private String descripcion;
    @NotNull @Positive
    private BigDecimal precioCompra;
    @NotNull @Positive
    private BigDecimal precioVenta;
    private Integer stock;
    private Integer stockMinimo;
    private String imagen;
    private Long idCategoria;
    private Long idMarca;
    private Long idProveedor;
}
