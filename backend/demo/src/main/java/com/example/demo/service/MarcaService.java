package com.example.demo.service;

import com.example.demo.model.Marca;
import java.util.List;

public interface MarcaService {
    List<Marca> listarTodas();
    Marca obtenerPorId(Long id);
    Marca crear(Marca marca);
    Marca actualizar(Long id, Marca marca);
    void eliminar(Long id);
}
