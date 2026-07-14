package com.example.demo.repository;

import com.example.demo.model.EstadoVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstadoVentaRepository extends JpaRepository<EstadoVenta, Long> {
    Optional<EstadoVenta> findByNombre(String nombre);
}
