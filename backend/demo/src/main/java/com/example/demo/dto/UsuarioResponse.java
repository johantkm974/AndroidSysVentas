package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor @Builder
public class UsuarioResponse {
    private Long idUsuario;
    private String nombres;
    private String apellidos;
    private String dni;
    private String telefono;
    private String direccion;
    private String correo;
    private Boolean activo;
    private List<String> roles;
}
