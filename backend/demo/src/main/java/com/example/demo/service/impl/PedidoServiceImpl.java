package com.example.demo.service.impl;

import com.example.demo.dto.PedidoRequest;
import com.example.demo.dto.PedidoResponse;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;

    @Override
    public List<PedidoResponse> listarTodos() {
        return pedidoRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PedidoResponse obtenerPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + id));
        return toResponse(pedido);
    }

    @Override
    public PedidoResponse obtenerPorNumero(String numeroPedido) {
        Pedido pedido = pedidoRepository.findByNumeroPedido(numeroPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + numeroPedido));
        return toResponse(pedido);
    }

    @Override
    @Transactional
    public PedidoResponse crear(PedidoRequest request, Long idUsuarioCliente) {
        Usuario cliente = usuarioRepository.findById(idUsuarioCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        EstadoPedido estadoPendiente = estadoPedidoRepository.findByNombre("PENDIENTE")
                .orElseThrow(() -> new RuntimeException("Estado PENDIENTE no configurado"));

        String numeroPedido = generarNumeroPedido();

        Pedido pedido = Pedido.builder()
                .numeroPedido(numeroPedido)
                .cliente(cliente)
                .estadoPedido(estadoPendiente)
                .total(BigDecimal.ZERO)
                .observacion(request.getObservacion())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (PedidoRequest.ItemPedido item : request.getItems()) {
            Producto producto = productoRepository.findById(item.getIdProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + item.getIdProducto()));

            if (!producto.getActivo()) {
                throw new BadRequestException("Producto no disponible: " + producto.getNombre());
            }

            BigDecimal subtotal = producto.getPrecioVenta().multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);

            DetallePedido detalle = DetallePedido.builder()
                    .pedido(pedido)
                    .producto(producto)
                    .cantidad(item.getCantidad())
                    .precioUnitario(producto.getPrecioVenta())
                    .subtotal(subtotal)
                    .build();
            pedido.getDetalles().add(detalle);
        }

        pedido.setTotal(total);
        pedido = pedidoRepository.save(pedido);

        return toResponse(pedido);
    }

    @Override
    public List<PedidoResponse> listarPorCliente(Long idUsuario) {
        return pedidoRepository.findByClienteIdUsuarioOrderByCreatedAtDesc(idUsuario).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PedidoResponse cambiarEstado(Long idPedido, Long idEstadoPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + idPedido));
        EstadoPedido estado = estadoPedidoRepository.findById(idEstadoPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Estado no encontrado: " + idEstadoPedido));
        pedido.setEstadoPedido(estado);
        return toResponse(pedidoRepository.save(pedido));
    }

    @Override
    @Transactional
    public PedidoResponse cancelarPorCliente(Long idPedido, Long idUsuarioCliente) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + idPedido));

        if (!Objects.equals(pedido.getCliente().getIdUsuario(), idUsuarioCliente)) {
            throw new BadRequestException("No puedes cancelar un pedido que no te pertenece");
        }
        if (!"PENDIENTE".equals(pedido.getEstadoPedido().getNombre())) {
            throw new BadRequestException("Solo puedes cancelar pedidos en estado PENDIENTE");
        }

        EstadoPedido cancelado = estadoPedidoRepository.findByNombre("CANCELADO")
                .orElseThrow(() -> new RuntimeException("Estado CANCELADO no configurado"));
        pedido.setEstadoPedido(cancelado);
        return toResponse(pedidoRepository.save(pedido));
    }

    private String generarNumeroPedido() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(9999);
        return "PED-" + timestamp + "-" + String.format("%04d", random);
    }

    private PedidoResponse toResponse(Pedido pedido) {
        List<PedidoResponse.DetallePedidoResponse> detalles = pedido.getDetalles().stream()
                .map(d -> PedidoResponse.DetallePedidoResponse.builder()
                        .idDetalle(d.getIdDetallePedido())
                        .producto(d.getProducto().getNombre())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return PedidoResponse.builder()
                .idPedido(pedido.getIdPedido())
                .numeroPedido(pedido.getNumeroPedido())
                .cliente(pedido.getCliente().getNombres() + " " + pedido.getCliente().getApellidos())
                .estado(pedido.getEstadoPedido().getNombre())
                .total(pedido.getTotal())
                .observacion(pedido.getObservacion())
                .createdAt(pedido.getCreatedAt())
                .detalles(detalles)
                .build();
    }
}
