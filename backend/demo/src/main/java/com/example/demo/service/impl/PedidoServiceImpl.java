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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private static final BigDecimal IGV_TASA = new BigDecimal("0.18");

    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final VentaRepository ventaRepository;
    private final EstadoVentaRepository estadoVentaRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final EnvioRepository envioRepository;
    private final EstadoEnvioRepository estadoEnvioRepository;
    private final SeguimientoEnvioRepository seguimientoRepository;
    private final MovimientoInventarioRepository movimientoRepository;

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

        EstadoPedido estadoConfirmado = estadoPedidoRepository.findByNombre("CONFIRMADO")
                .orElseThrow(() -> new RuntimeException("Estado CONFIRMADO no configurado"));

        String numeroPedido = generarNumeroPedido();

        Pedido pedido = Pedido.builder()
                .numeroPedido(numeroPedido)
                .cliente(cliente)
                .estadoPedido(estadoConfirmado)
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

            if (producto.getStock() < item.getCantidad()) {
                throw new BadRequestException(
                        "Stock insuficiente para " + producto.getNombre() +
                        " (disponible: " + producto.getStock() + ", requerido: " + item.getCantidad() + ")");
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

        // --- AUTOMATIZACIÓN: Crear Venta + Envío + descontar stock ---

        // 1. Descontar stock y registrar movimientos
        for (DetallePedido detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            int stockAnterior = producto.getStock();
            producto.setStock(stockAnterior - detalle.getCantidad());
            productoRepository.save(producto);

            MovimientoInventario movimiento = MovimientoInventario.builder()
                    .producto(producto)
                    .usuario(cliente)
                    .tipoMovimiento(MovimientoInventario.TipoMovimiento.SALIDA)
                    .cantidad(detalle.getCantidad())
                    .stockAnterior(stockAnterior)
                    .stockActual(producto.getStock())
                    .descripcion("Venta automática #" + generarNumeroVenta())
                    .build();
            movimientoRepository.save(movimiento);
        }

        // 2. Crear Venta con estado PAGADA
        MetodoPago metodoSimulado = metodoPagoRepository.findByNombre("SIMULADO")
                .orElseThrow(() -> new RuntimeException("Método de pago SIMULADO no configurado"));
        EstadoVenta estadoPagada = estadoVentaRepository.findByNombre("PAGADA")
                .orElseThrow(() -> new RuntimeException("Estado PAGADA no configurado"));

        BigDecimal subtotal = pedido.getTotal();
        BigDecimal igv = subtotal.multiply(IGV_TASA).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalVenta = subtotal.add(igv);

        String numeroVenta = generarNumeroVenta();

        Venta venta = Venta.builder()
                .numeroVenta(numeroVenta)
                .pedido(pedido)
                .cliente(cliente)
                .vendedor(cliente)
                .metodoPago(metodoSimulado)
                .subtotal(subtotal)
                .igv(igv)
                .total(totalVenta)
                .estadoVenta(estadoPagada)
                .build();

        venta = ventaRepository.save(venta);

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
        ventaRepository.save(venta);

        // 3. Crear Envío con estado PENDIENTE
        EstadoEnvio estadoPendiente = estadoEnvioRepository.findByNombre("PENDIENTE")
                .orElseThrow(() -> new RuntimeException("Estado PENDIENTE de envío no configurado"));

        String direccionEnvio = request.getDireccion() != null ? request.getDireccion() : (cliente.getDireccion() != null ? cliente.getDireccion() : "Por asignar");
        String distritoEnvio = request.getDistrito() != null ? request.getDistrito() : "Por asignar";
        Envio envio = Envio.builder()
                .pedido(pedido)
                .direccion(direccionEnvio)
                .distrito(distritoEnvio)
                .estadoEnvio(estadoPendiente)
                .build();

        envio = envioRepository.save(envio);

        SeguimientoEnvio seguimiento = SeguimientoEnvio.builder()
                .envio(envio)
                .estadoEnvio(estadoPendiente)
                .observacion("Envío generado automáticamente")
                .build();
        seguimientoRepository.save(seguimiento);

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

        // Si es ENTREGADO, delegar al flujo de Envío para mantener consistencia
        if ("ENTREGADO".equals(estado.getNombre())) {
            Envio envio = envioRepository.findByPedidoIdPedido(idPedido)
                    .orElseThrow(() -> new RuntimeException("No se encontró envío para el pedido"));
            EstadoEnvio estadoEntregado = estadoEnvioRepository.findByNombre("ENTREGADO")
                    .orElseThrow(() -> new RuntimeException("Estado ENTREGADO de envío no configurado"));
            envio.setEstadoEnvio(estadoEntregado);
            envio.setFechaEntrega(java.time.LocalDateTime.now());

            SeguimientoEnvio seguimiento = SeguimientoEnvio.builder()
                    .envio(envio)
                    .estadoEnvio(estadoEntregado)
                    .observacion("Entregado - confirmado por administrador")
                    .build();
            seguimientoRepository.save(seguimiento);

            envioRepository.save(envio);

            // Actualizar Pedido y Venta
            pedido.setEstadoPedido(estado);
            if (pedido.getVenta() != null) {
                EstadoVenta pagada = estadoVentaRepository.findByNombre("PAGADA")
                        .orElseThrow(() -> new RuntimeException("Estado PAGADA no configurado"));
                pedido.getVenta().setEstadoVenta(pagada);
                ventaRepository.save(pedido.getVenta());
            }
            return toResponse(pedidoRepository.save(pedido));
        }

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

        String estadoActual = pedido.getEstadoPedido().getNombre();
        if (!"PENDIENTE".equals(estadoActual) && !"CONFIRMADO".equals(estadoActual)) {
            throw new BadRequestException("Solo puedes cancelar pedidos en estado PENDIENTE o CONFIRMADO");
        }

        // Anular Venta si existe
        Venta venta = ventaRepository.findByPedidoIdPedido(idPedido).orElse(null);
        if (venta != null && !"ANULADA".equals(venta.getEstadoVenta().getNombre())) {
            EstadoVenta anulada = estadoVentaRepository.findByNombre("ANULADA")
                    .orElseThrow(() -> new RuntimeException("Estado ANULADA no configurado"));

            // Restaurar stock
            for (DetalleVenta dv : venta.getDetalles()) {
                Producto producto = dv.getProducto();
                int stockAnterior = producto.getStock();
                producto.setStock(stockAnterior + dv.getCantidad());
                productoRepository.save(producto);

                MovimientoInventario movimiento = MovimientoInventario.builder()
                        .producto(producto)
                        .usuario(pedido.getCliente())
                        .tipoMovimiento(MovimientoInventario.TipoMovimiento.ENTRADA)
                        .cantidad(dv.getCantidad())
                        .stockAnterior(stockAnterior)
                        .stockActual(producto.getStock())
                        .descripcion("Devolución por cancelación de pedido #" + pedido.getNumeroPedido())
                        .build();
                movimientoRepository.save(movimiento);
            }

            venta.setEstadoVenta(anulada);
            ventaRepository.save(venta);
        }

        // Cancelar Envío si existe
        Envio envio = envioRepository.findByPedidoIdPedido(idPedido).orElse(null);
        if (envio != null && !"CANCELADO".equals(envio.getEstadoEnvio().getNombre())) {
            EstadoEnvio cancelado = estadoEnvioRepository.findByNombre("CANCELADO")
                    .orElseThrow(() -> new RuntimeException("Estado CANCELADO de envío no configurado"));
            envio.setEstadoEnvio(cancelado);
            envioRepository.save(envio);
        }

        EstadoPedido estadoCancelado = estadoPedidoRepository.findByNombre("CANCELADO")
                .orElseThrow(() -> new RuntimeException("Estado CANCELADO no configurado"));
        pedido.setEstadoPedido(estadoCancelado);
        return toResponse(pedidoRepository.save(pedido));
    }

    private String generarNumeroPedido() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(9999);
        return "PED-" + timestamp + "-" + String.format("%04d", random);
    }

    private String generarNumeroVenta() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = new Random().nextInt(9999);
        return "VEN-" + timestamp + "-" + String.format("%04d", random);
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

        Envio envio = envioRepository.findByPedidoIdPedido(pedido.getIdPedido()).orElse(null);

        String repartidorNombre = null;
        if (envio != null && envio.getRepartidor() != null) {
            repartidorNombre = envio.getRepartidor().getNombres() + " " + envio.getRepartidor().getApellidos();
        }

        return PedidoResponse.builder()
                .idPedido(pedido.getIdPedido())
                .numeroPedido(pedido.getNumeroPedido())
                .cliente(pedido.getCliente().getNombres() + " " + pedido.getCliente().getApellidos())
                .estado(pedido.getEstadoPedido().getNombre())
                .total(pedido.getTotal())
                .observacion(pedido.getObservacion())
                .createdAt(pedido.getCreatedAt())
                .detalles(detalles)
                .idEnvio(envio != null ? envio.getIdEnvio() : null)
                .estadoEnvio(envio != null ? envio.getEstadoEnvio().getNombre() : null)
                .repartidor(repartidorNombre)
                .build();
    }
}
