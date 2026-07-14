package com.example.demo.repository;

import com.example.demo.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByNumeroPedido(String numeroPedido);
    List<Pedido> findByClienteIdUsuarioOrderByCreatedAtDesc(Long idUsuario);
    List<Pedido> findByEstadoPedidoIdEstadoPedido(Long idEstadoPedido);
}
