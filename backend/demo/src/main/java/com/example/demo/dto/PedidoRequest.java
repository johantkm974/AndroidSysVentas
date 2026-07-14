package com.example.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class PedidoRequest {
    @NotEmpty
    private List<ItemPedido> items;
    private String observacion;

    @Data
    public static class ItemPedido {
        @NotNull
        private Long idProducto;
        @NotNull
        private Integer cantidad;
    }
}
