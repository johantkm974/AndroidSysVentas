package com.example.demo.security;

import com.example.demo.model.Credencial;
import com.example.demo.model.UsuarioRol;
import com.example.demo.repository.CredencialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final CredencialRepository credencialRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Credencial credencial = credencialRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + correo));

        if (!credencial.getActivo() || !credencial.getUsuario().getActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo");
        }

        List<GrantedAuthority> authorities = credencial.getUsuario().getUsuariosRoles().stream()
                .map(UsuarioRol::getRol)
                .map(rol -> new SimpleGrantedAuthority(rol.getNombre()))
                .collect(Collectors.toList());

        return new User(credencial.getCorreo(), credencial.getPassword(), authorities);
    }
}
