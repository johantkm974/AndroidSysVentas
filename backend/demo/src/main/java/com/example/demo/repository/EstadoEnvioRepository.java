package com.example.demo.repository;

import com.example.demo.model.EstadoEnvio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstadoEnvioRepository extends JpaRepository<EstadoEnvio, Long> {
    Optional<EstadoEnvio> findByNombre(String nombre);
}
