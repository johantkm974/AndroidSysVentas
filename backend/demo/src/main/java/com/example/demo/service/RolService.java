package com.example.demo.service;

import com.example.demo.model.Rol;
import java.util.List;

public interface RolService {
    List<Rol> listarTodos();
    Rol obtenerPorId(Long id);
}
