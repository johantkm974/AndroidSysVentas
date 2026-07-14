package com.example.demo.repository;

import com.example.demo.model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {
    List<MovimientoInventario> findByProductoIdProductoOrderByCreatedAtDesc(Long idProducto);
}
