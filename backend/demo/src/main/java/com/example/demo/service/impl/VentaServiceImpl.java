package com.example.demo.service.impl;

import com.example.demo.dto.VentaRequest;
import com.example.demo.dto.VentaResponse;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private static final BigDecimal IGV_TASA = new BigDecimal("0.18");

    private final VentaRepository ventaRepository;
    private final PedidoRepository pedidoRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final EstadoVentaRepository estadoVentaRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;

    @Override
    public List<VentaResponse> listarTodas() {
        return ventaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public VentaResponse obtenerPorId(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada: " + id));
        return toResponse(venta);
    }

    @Override
    @Transactional
    public VentaResponse procesarVenta(VentaRequest request, Long idUsuarioVendedor) {
        Pedido pedido = pedidoRepository.findById(request.getIdPedido())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        if (!"PENDIENTE".equals(pedido.getEstadoPedido().getNombre())) {
            throw new BadRequestException("El pedido ya fue procesado o cancelado");
        }

        MetodoPago metodoPago = metodoPagoRepository.findById(request.getIdMetodoPago())
                .orElseThrow(() -> new ResourceNotFoundException("Método de pago no encontrado"));

        EstadoVenta estadoPendiente = estadoVentaRepository.findByNombre("PENDIENTE")
                .orElseThrow(() -> new RuntimeException("Estado PENDIENTE no configurado"));

        EstadoPedido estadoConfirmado = estadoPedidoRepository.findByNombre("CONFIRMADO")
                .orElseThrow(() -> new RuntimeException("Estado CONFIRMADO no configurado"));

        // Validar y descontar stock
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            if (producto.getStock() < detalle.getCantidad()) {
                throw new BadRequestException(
                        "Stock insuficiente para " + producto.getNombre() +
                        " (disponible: " + producto.getStock() + ", requerido: " + detalle.getCantidad() + ")");
            }
        }

        // Descontar stock y registrar movimientos
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            int stockAnterior = producto.getStock();
            producto.setStock(stockAnterior - detalle.getCantidad());
            productoRepository.save(producto);

            MovimientoInventario movimiento = MovimientoInventario.builder()
                    .producto(producto)
                    .usuario(pedido.getCliente())
                    .tipoMovimiento(MovimientoInventario.TipoMovimiento.SALIDA)
                    .cantidad(detalle.getCantidad())
                    .stockAnterior(stockAnterior)
                    .stockActual(producto.getStock())
                    .descripcion("Venta #" + generarNumeroVenta())
                    .build();
            movimientoRepository.save(movimiento);
        }

        // Calcular montos
        BigDecimal subtotal = pedido.getTotal();
        BigDecimal igv = subtotal.multiply(IGV_TASA).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(igv);

        Usuario vendedor = usuarioRepository.findById(idUsuarioVendedor)
                .orElseThrow(() -> new ResourceNotFoundException("Vendedor no encontrado"));

        String numeroVenta = generarNumeroVenta();

        Venta venta = Venta.builder()
                .numeroVenta(numeroVenta)
                .pedido(pedido)
                .cliente(pedido.getCliente())
                .vendedor(vendedor)
                .metodoPago(metodoPago)
                .subtotal(subtotal)
                .igv(igv)
                .total(total)
                .estadoVenta(estadoPendiente)
                .build();

        venta = ventaRepository.save(venta);

        // Crear detalle de venta desde el detalle del pedido
        for (DetallePedido detalle : pedido.getDetalles()) {
            DetalleVenta dv = DetalleVenta.builder()
                    .venta(venta)
                    .producto(detalle.getProducto())
                    .cantidad(detalle.getCantidad())
                    .precioUnitario(detalle.getPrecioUnitario())
                    .subtotal(detalle.getSubtotal())
                    .build();
            venta.getDetalles().add(dv);
        }

        // Actualizar estado del pedido
        pedido.setEstadoPedido(estadoConfirmado);
        pedidoRepository.save(pedido);

        return toResponse(ventaRepository.save(venta));
    }

    @Override
    @Transactional
    public VentaResponse anularVenta(Long idVenta) {
        Venta venta = ventaRepository.findById(idVenta)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada: " + idVenta));

        if (!"PENDIENTE".equals(venta.getEstadoVenta().getNombre())) {
            throw new BadRequestException("Solo se pueden anular ventas en estado PENDIENTE");
        }

        EstadoVenta anulada = estadoVentaRepository.findByNombre("ANULADA")
                .orElseThrow(() -> new RuntimeException("Estado ANULADA no configurado"));

        EstadoPedido pendiente = estadoPedidoRepository.findByNombre("PENDIENTE")
                .orElseThrow(() -> new RuntimeException("Estado PENDIENTE no configurado"));

        // Devolver stock
        Pedido pedido = venta.getPedido();
        for (DetalleVenta detalle : venta.getDetalles()) {
            Producto producto = detalle.getProducto();
            int stockAnterior = producto.getStock();
            producto.setStock(stockAnterior + detalle.getCantidad());
            productoRepository.save(producto);

            MovimientoInventario movimiento = MovimientoInventario.builder()
                    .producto(producto)
                    .usuario(venta.getCliente())
                    .tipoMovimiento(MovimientoInventario.TipoMovimiento.ENTRADA)
                    .cantidad(detalle.getCantidad())
                    .stockAnterior(stockAnterior)
                    .stockActual(producto.getStock())
                    .descripcion("Devolución por anulación de venta #" + venta.getNumeroVenta())
                    .build();
            movimientoRepository.save(movimiento);
        }

        venta.setEstadoVenta(anulada);
        pedido.setEstadoPedido(pendiente);

        return toResponse(ventaRepository.save(venta));
    }

    private String generarNumeroVenta() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(9999);
        return "VEN-" + timestamp + "-" + String.format("%04d", random);
    }

    private VentaResponse toResponse(Venta venta) {
        List<VentaResponse.DetalleVentaResponse> detalles = venta.getDetalles().stream()
                .map(d -> VentaResponse.DetalleVentaResponse.builder()
                        .producto(d.getProducto().getNombre())
                        .cantidad(d.getCantidad())
                        .precioUnitario(d.getPrecioUnitario())
                        .subtotal(d.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return VentaResponse.builder()
                .idVenta(venta.getIdVenta())
                .numeroVenta(venta.getNumeroVenta())
                .numeroPedido(venta.getPedido().getNumeroPedido())
                .cliente(venta.getCliente().getNombres() + " " + venta.getCliente().getApellidos())
                .metodoPago(venta.getMetodoPago().getNombre())
                .subtotal(venta.getSubtotal())
                .igv(venta.getIgv())
                .total(venta.getTotal())
                .estado(venta.getEstadoVenta().getNombre())
                .createdAt(venta.getCreatedAt())
                .detalles(detalles)
                .build();
    }
}
