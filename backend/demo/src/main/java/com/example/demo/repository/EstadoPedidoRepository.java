package com.example.demo.repository;

import com.example.demo.model.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstadoPedidoRepository extends JpaRepository<EstadoPedido, Long> {
    Optional<EstadoPedido> findByNombre(String nombre);
}
