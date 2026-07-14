package com.example.demo.repository;

import com.example.demo.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface VentaRepository extends JpaRepository<Venta, Long> {
    Optional<Venta> findByNumeroVenta(String numeroVenta);
    Optional<Venta> findByPedidoIdPedido(Long idPedido);
    List<Venta> findByClienteIdUsuarioOrderByCreatedAtDesc(Long idUsuario);
}
