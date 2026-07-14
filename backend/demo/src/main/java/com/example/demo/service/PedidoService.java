package com.example.demo.service;

import com.example.demo.dto.PedidoRequest;
import com.example.demo.dto.PedidoResponse;
import java.util.List;

public interface PedidoService {
    List<PedidoResponse> listarTodos();
    PedidoResponse obtenerPorId(Long id);
    PedidoResponse obtenerPorNumero(String numeroPedido);
    PedidoResponse crear(PedidoRequest request, Long idUsuarioCliente);
    List<PedidoResponse> listarPorCliente(Long idUsuario);
    PedidoResponse cambiarEstado(Long idPedido, Long idEstadoPedido);
    PedidoResponse cancelarPorCliente(Long idPedido, Long idUsuarioCliente);
}
