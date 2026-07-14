package com.example.demo.service;

import com.example.demo.model.Categoria;
import java.util.List;

public interface CategoriaService {
    List<Categoria> listarTodas();
    Categoria obtenerPorId(Long id);
    Categoria crear(Categoria categoria);
    Categoria actualizar(Long id, Categoria categoria);
    void eliminar(Long id);
}
