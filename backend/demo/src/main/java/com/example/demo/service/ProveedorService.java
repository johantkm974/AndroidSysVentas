package com.example.demo.service;

import com.example.demo.model.Proveedor;
import java.util.List;

public interface ProveedorService {
    List<Proveedor> listarTodos();
    Proveedor obtenerPorId(Long id);
    Proveedor crear(Proveedor proveedor);
    Proveedor actualizar(Long id, Proveedor proveedor);
    void eliminar(Long id);
}
