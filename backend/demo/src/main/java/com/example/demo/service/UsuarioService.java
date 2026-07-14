package com.example.demo.service;

import com.example.demo.dto.ActualizarUsuarioRequest;
import com.example.demo.dto.CrearUsuarioRequest;
import com.example.demo.dto.UsuarioResponse;
import java.util.List;

public interface UsuarioService {
    List<UsuarioResponse> listarTodos();
    UsuarioResponse obtenerPorId(Long id);
    UsuarioResponse obtenerPorCorreo(String correo);
    UsuarioResponse crear(CrearUsuarioRequest request);
    UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest request);
    void eliminar(Long id);
    UsuarioResponse obtenerPerfilPorToken(Long idUsuario);
}
