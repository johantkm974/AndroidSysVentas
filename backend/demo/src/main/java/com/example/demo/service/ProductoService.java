package com.example.demo.service;

import com.example.demo.dto.ProductoRequest;
import com.example.demo.dto.ProductoResponse;
import java.util.List;

public interface ProductoService {
    List<ProductoResponse> listarTodos();
    ProductoResponse obtenerPorId(Long id);
    ProductoResponse obtenerPorCodigo(String codigo);
    ProductoResponse crear(ProductoRequest request);
    ProductoResponse actualizar(Long id, ProductoRequest request);
    void eliminar(Long id);
    void eliminarPermanentemente(Long id);
    List<ProductoResponse> buscarPorCategoria(Long idCategoria);
    List<ProductoResponse> buscarPorMarca(Long idMarca);
    List<ProductoResponse> buscarPorProveedor(Long idProveedor);
    List<ProductoResponse> productosConStockBajo();
    List<ProductoResponse> listarActivos();
}
