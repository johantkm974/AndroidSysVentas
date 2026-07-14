package com.example.demo.service;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegistrarUsuarioRequest;
import com.example.demo.dto.UsuarioResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    UsuarioResponse register(RegistrarUsuarioRequest request);
}
