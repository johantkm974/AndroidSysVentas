package com.example.demo.repository;

import com.example.demo.model.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
    Optional<MetodoPago> findByNombre(String nombre);
}
