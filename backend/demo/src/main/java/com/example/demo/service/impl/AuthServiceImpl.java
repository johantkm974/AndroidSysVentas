package com.example.demo.service.impl;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.dto.RegistrarUsuarioRequest;
import com.example.demo.dto.UsuarioResponse;
import com.example.demo.exception.BadRequestException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.security.JwtTokenProvider;
import com.example.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final CredencialRepository credencialRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioRolRepository usuarioRolRepository;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getPassword()));

        Credencial credencial = credencialRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new BadRequestException("Credenciales inválidas"));

        Usuario usuario = credencial.getUsuario();
        List<String> roles = usuario.getUsuariosRoles().stream()
                .map(ur -> ur.getRol().getNombre())
                .collect(Collectors.toList());

        String token = jwtTokenProvider.generarToken(usuario.getIdUsuario(), credencial.getCorreo(), roles);

        return LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .idUsuario(usuario.getIdUsuario())
                .nombres(usuario.getNombres() + " " + usuario.getApellidos())
                .correo(credencial.getCorreo())
                .roles(roles)
                .build();
    }

    @Override
    @Transactional
    public UsuarioResponse register(RegistrarUsuarioRequest request) {
        if (usuarioRepository.existsByDni(request.getDni())) {
            throw new BadRequestException("El DNI ya está registrado");
        }
        if (credencialRepository.existsByCorreo(request.getCorreo())) {
            throw new BadRequestException("El correo ya está registrado");
        }

        Usuario usuario = Usuario.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .dni(request.getDni())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .activo(true)
                .build();
        usuario = usuarioRepository.save(usuario);

        Credencial credencial = Credencial.builder()
                .usuario(usuario)
                .correo(request.getCorreo())
                .password(passwordEncoder.encode(request.getPassword()))
                .activo(true)
                .build();
        credencialRepository.save(credencial);

        Rol rolCliente = rolRepository.findByNombre("ROLE_CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));

        UsuarioRol usuarioRol = UsuarioRol.builder()
                .usuario(usuario)
                .rol(rolCliente)
                .build();
        usuarioRolRepository.save(usuarioRol);

        List<String> roles = List.of(rolCliente.getNombre());

        return UsuarioResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .dni(usuario.getDni())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .correo(credencial.getCorreo())
                .activo(usuario.getActivo())
                .roles(roles)
                .build();
    }
}
