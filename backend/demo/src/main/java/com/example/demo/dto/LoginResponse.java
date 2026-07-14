package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data @AllArgsConstructor @Builder
public class LoginResponse {
    private String token;
    private String tipo;
    private Long idUsuario;
    private String nombres;
    private String correo;
    private List<String> roles;
}
