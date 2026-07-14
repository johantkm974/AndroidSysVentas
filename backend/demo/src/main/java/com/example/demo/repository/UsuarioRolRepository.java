package com.example.demo.repository;

import com.example.demo.model.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {
    List<UsuarioRol> findByUsuarioIdUsuario(Long idUsuario);
    void deleteByUsuarioIdUsuario(Long idUsuario);
}
