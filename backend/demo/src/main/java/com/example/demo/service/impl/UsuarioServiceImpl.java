package com.example.demo.service.impl;

import com.example.demo.dto.ActualizarUsuarioRequest;
import com.example.demo.dto.CrearUsuarioRequest;
import com.example.demo.dto.UsuarioResponse;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ForbiddenException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Credencial;
import com.example.demo.model.Rol;
import com.example.demo.model.Usuario;
import com.example.demo.model.UsuarioRol;
import com.example.demo.repository.CredencialRepository;
import com.example.demo.repository.RolRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.UsuarioRolRepository;
import com.example.demo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioRolRepository usuarioRolRepository;
    private final RolRepository rolRepository;
    private final CredencialRepository credencialRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream()
                .filter(Usuario::getActivo)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioResponse obtenerPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
        return toResponse(usuario);
    }

    @Override
    public UsuarioResponse obtenerPorCorreo(String correo) {
        Usuario usuario = usuarioRepository.findByCredencialCorreo(correo)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + correo));
        return toResponse(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse crear(CrearUsuarioRequest request) {
        if (usuarioRepository.existsByDniAndActivoTrue(request.getDni())) {
            throw new BadRequestException("El DNI ya está registrado");
        }
        if (credencialRepository.existsByCorreoAndActivoTrue(request.getCorreo())) {
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
        usuario.setCredencial(credencial);

        Set<UsuarioRol> usuariosRoles = new HashSet<>();
        for (Long idRol : request.getIdRoles()) {
            Rol rol = rolRepository.findById(idRol)
                    .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + idRol));
            usuariosRoles.add(UsuarioRol.builder().usuario(usuario).rol(rol).build());
        }
        usuarioRolRepository.saveAll(usuariosRoles);
        usuario.setUsuariosRoles(usuariosRoles);

        return toResponse(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));

        if (request.getNombres() != null) usuario.setNombres(request.getNombres());
        if (request.getApellidos() != null) usuario.setApellidos(request.getApellidos());
        if (request.getDni() != null) {
            if (!usuario.getDni().equals(request.getDni()) && usuarioRepository.existsByDni(request.getDni())) {
                throw new BadRequestException("El DNI ya está registrado");
            }
            usuario.setDni(request.getDni());
        }
        if (request.getTelefono() != null) usuario.setTelefono(request.getTelefono());
        if (request.getDireccion() != null) usuario.setDireccion(request.getDireccion());
        if (request.getActivo() != null) usuario.setActivo(request.getActivo());

        if (request.getIdRoles() != null) {
            usuarioRolRepository.deleteByUsuarioIdUsuario(id);
            Set<UsuarioRol> nuevosRoles = new HashSet<>();
            for (Long idRol : request.getIdRoles()) {
                Rol rol = rolRepository.findById(idRol)
                        .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + idRol));
                nuevosRoles.add(UsuarioRol.builder().usuario(usuario).rol(rol).build());
            }
            usuario.setUsuariosRoles(nuevosRoles);
        }

        return toResponse(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));

        boolean esAdmin = usuario.getUsuariosRoles().stream()
                .anyMatch(ur -> ur.getRol().getNombre().equals("ROLE_ADMIN"));

        if (esAdmin) {
            long adminsActivos = usuarioRepository.findAll().stream()
                    .filter(Usuario::getActivo)
                    .filter(u -> u.getUsuariosRoles().stream()
                            .anyMatch(ur -> ur.getRol().getNombre().equals("ROLE_ADMIN")))
                    .count();
            if (adminsActivos <= 1) {
                throw new ForbiddenException("No se puede eliminar el único administrador del sistema");
            }
        }

        usuario.setActivo(false);
        usuarioRepository.save(usuario);

        Credencial credencial = usuario.getCredencial();
        if (credencial != null) {
            credencial.setActivo(false);
            credencialRepository.save(credencial);
        }
    }

    @Override
    public UsuarioResponse obtenerPerfilPorToken(Long idUsuario) {
        return obtenerPorId(idUsuario);
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        List<String> roles = usuario.getUsuariosRoles().stream()
                .map(ur -> ur.getRol().getNombre())
                .collect(Collectors.toList());

        return UsuarioResponse.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .dni(usuario.getDni())
                .telefono(usuario.getTelefono())
                .direccion(usuario.getDireccion())
                .correo(usuario.getCredencial().getCorreo())
                .activo(usuario.getActivo())
                .roles(roles)
                .build();
    }
}
