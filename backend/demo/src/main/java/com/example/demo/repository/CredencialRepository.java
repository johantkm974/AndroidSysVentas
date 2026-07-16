package com.example.demo.repository;

import com.example.demo.model.Credencial;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CredencialRepository extends JpaRepository<Credencial, Long> {
    Optional<Credencial> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    boolean existsByCorreoAndActivoTrue(String correo);
}
