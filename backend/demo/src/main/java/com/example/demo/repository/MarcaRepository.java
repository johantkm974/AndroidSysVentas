package com.example.demo.repository;

import com.example.demo.model.Marca;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MarcaRepository extends JpaRepository<Marca, Long> {
    Optional<Marca> findByNombre(String nombre);
}
