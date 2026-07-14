package com.example.demo.service;

import com.example.demo.model.MovimientoInventario;
import java.util.List;

public interface MovimientoInventarioService {
    List<MovimientoInventario> listarTodos();
    MovimientoInventario obtenerPorId(Long id);
    List<MovimientoInventario> listarPorProducto(Long idProducto);
    MovimientoInventario registrarEntrada(Long idProducto, Long idUsuario, Integer cantidad, String descripcion);
    MovimientoInventario registrarSalida(Long idProducto, Long idUsuario, Integer cantidad, String descripcion);
    MovimientoInventario registrarAjuste(Long idProducto, Long idUsuario, Integer nuevoStock, String descripcion);
}
