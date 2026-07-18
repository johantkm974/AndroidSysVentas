package com.example.demo.service.impl;

import com.example.demo.dto.EnvioRequest;
import com.example.demo.dto.EnvioResponse;
import com.example.demo.dto.EstadoEnvioDto;
import com.example.demo.dto.SeguimientoResponse;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.EnvioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnvioServiceImpl implements EnvioService {

    private final EnvioRepository envioRepository;
    private final PedidoRepository pedidoRepository;
    private final EstadoEnvioRepository estadoEnvioRepository;
    private final SeguimientoEnvioRepository seguimientoRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final VentaRepository ventaRepository;
    private final EstadoVentaRepository estadoVentaRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<EnvioResponse> listarTodos() {
        return envioRepository.findAll().stream()
                .map(this::toEnvioResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EnvioResponse obtenerPorId(Long id) {
        Envio envio = envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado: " + id));
        return toEnvioResponse(envio);
    }

    @Override
    public EnvioResponse obtenerPorPedido(Long idPedido) {
        Envio envio = envioRepository.findByPedidoIdPedido(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado para el pedido: " + idPedido));
        return toEnvioResponse(envio);
    }

    @Override
    @Transactional
    public EnvioResponse crear(EnvioRequest request) {
        Pedido pedido = pedidoRepository.findById(request.getIdPedido())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        if (envioRepository.findByPedidoIdPedido(request.getIdPedido()).isPresent()) {
            throw new BadRequestException("El pedido ya tiene un envío registrado");
        }

        EstadoEnvio estadoPendiente = estadoEnvioRepository.findByNombre("PENDIENTE")
                .orElseThrow(() -> new RuntimeException("Estado PENDIENTE no configurado"));

        Envio envio = Envio.builder()
                .pedido(pedido)
                .direccion(request.getDireccion())
                .distrito(request.getDistrito())
                .referencia(request.getReferencia())
                .estadoEnvio(estadoPendiente)
                .build();

        envio = envioRepository.save(envio);

        SeguimientoEnvio seguimiento = SeguimientoEnvio.builder()
                .envio(envio)
                .estadoEnvio(estadoPendiente)
                .observacion("Envío creado")
                .build();
        seguimientoRepository.save(seguimiento);

        return toEnvioResponse(envio);
    }

    @Override
    @Transactional
    public EnvioResponse actualizarEstado(Long idEnvio, Long idEstadoEnvio, String observacion) {
        Envio envio = envioRepository.findById(idEnvio)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado: " + idEnvio));

        EstadoEnvio nuevoEstado = estadoEnvioRepository.findById(idEstadoEnvio)
                .orElseThrow(() -> new ResourceNotFoundException("Estado de envío no encontrado: " + idEstadoEnvio));

        envio.setEstadoEnvio(nuevoEstado);
        Pedido pedido = envio.getPedido();

        switch (nuevoEstado.getNombre()) {
            case "EN_RUTA":
                envio.setFechaEnvio(LocalDateTime.now());
                EstadoPedido enviado = estadoPedidoRepository.findByNombre("ENVIADO")
                        .orElseThrow(() -> new RuntimeException("Estado ENVIADO no configurado"));
                pedido.setEstadoPedido(enviado);
                pedidoRepository.save(pedido);
                break;

            case "ENTREGADO":
                envio.setFechaEntrega(LocalDateTime.now());
                EstadoPedido entregado = estadoPedidoRepository.findByNombre("ENTREGADO")
                        .orElseThrow(() -> new RuntimeException("Estado ENTREGADO no configurado"));
                pedido.setEstadoPedido(entregado);
                pedidoRepository.save(pedido);

                Venta venta = ventaRepository.findByPedidoIdPedido(pedido.getIdPedido()).orElse(null);
                if (venta != null) {
                    EstadoVenta pagada = estadoVentaRepository.findByNombre("PAGADA")
                            .orElseThrow(() -> new RuntimeException("Estado PAGADA no configurado"));
                    venta.setEstadoVenta(pagada);
                    ventaRepository.save(venta);
                }
                break;

            case "CANCELADO":
                EstadoPedido cancelado = estadoPedidoRepository.findByNombre("CANCELADO")
                        .orElseThrow(() -> new RuntimeException("Estado CANCELADO no configurado"));
                pedido.setEstadoPedido(cancelado);
                pedidoRepository.save(pedido);

                Venta ventaCancel = ventaRepository.findByPedidoIdPedido(pedido.getIdPedido()).orElse(null);
                if (ventaCancel != null && !"ANULADA".equals(ventaCancel.getEstadoVenta().getNombre())) {
                    EstadoVenta anulada = estadoVentaRepository.findByNombre("ANULADA")
                            .orElseThrow(() -> new RuntimeException("Estado ANULADA no configurado"));

                    for (DetalleVenta dv : ventaCancel.getDetalles()) {
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
                                .descripcion("Devolución por cancelación de envío #" + envio.getIdEnvio())
                                .build();
                        movimientoRepository.save(movimiento);
                    }

                    ventaCancel.setEstadoVenta(anulada);
                    ventaRepository.save(ventaCancel);
                }
                break;
        }

        SeguimientoEnvio seguimiento = SeguimientoEnvio.builder()
                .envio(envio)
                .estadoEnvio(nuevoEstado)
                .observacion(observacion)
                .build();
        seguimientoRepository.save(seguimiento);

        return toEnvioResponse(envioRepository.save(envio));
    }

    @Override
    public List<SeguimientoResponse> obtenerTracking(Long idEnvio) {
        return seguimientoRepository.findByEnvioIdEnvioOrderByCreatedAtDesc(idEnvio).stream()
                .map(this::toSeguimientoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnvioResponse> listarPorRepartidor(Long idRepartidor) {
        return envioRepository.findByRepartidorIdUsuario(idRepartidor).stream()
                .map(this::toEnvioResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EnvioResponse asignarRepartidor(Long idEnvio, Long idRepartidor) {
        Envio envio = envioRepository.findById(idEnvio)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado: " + idEnvio));

        Usuario repartidor = usuarioRepository.findById(idRepartidor)
                .orElseThrow(() -> new ResourceNotFoundException("Repartidor no encontrado: " + idRepartidor));

        envio.setRepartidor(repartidor);

        SeguimientoEnvio seguimiento = SeguimientoEnvio.builder()
                .envio(envio)
                .estadoEnvio(envio.getEstadoEnvio())
                .observacion("Repartidor asignado: " + repartidor.getNombres() + " " + repartidor.getApellidos())
                .build();
        seguimientoRepository.save(seguimiento);

        return toEnvioResponse(envioRepository.save(envio));
    }

    private EnvioResponse toEnvioResponse(Envio envio) {
        EstadoEnvioDto estadoDto = null;
        if (envio.getEstadoEnvio() != null) {
            estadoDto = EstadoEnvioDto.builder()
                    .idEstadoEnvio(envio.getEstadoEnvio().getIdEstadoEnvio())
                    .nombre(envio.getEstadoEnvio().getNombre())
                    .descripcion(envio.getEstadoEnvio().getDescripcion())
                    .build();
        }

        String repartidorNombre = null;
        if (envio.getRepartidor() != null) {
            repartidorNombre = envio.getRepartidor().getNombres() + " " + envio.getRepartidor().getApellidos();
        }

        return EnvioResponse.builder()
                .idEnvio(envio.getIdEnvio())
                .direccion(envio.getDireccion())
                .distrito(envio.getDistrito())
                .referencia(envio.getReferencia())
                .estadoEnvio(estadoDto)
                .repartidor(repartidorNombre)
                .fechaEnvio(envio.getFechaEnvio() != null ? envio.getFechaEnvio().toString() : null)
                .fechaEntrega(envio.getFechaEntrega() != null ? envio.getFechaEntrega().toString() : null)
                .createdAt(envio.getCreatedAt() != null ? envio.getCreatedAt().toString() : null)
                .build();
    }

    private SeguimientoResponse toSeguimientoResponse(SeguimientoEnvio seg) {
        EstadoEnvioDto estadoDto = null;
        if (seg.getEstadoEnvio() != null) {
            estadoDto = EstadoEnvioDto.builder()
                    .idEstadoEnvio(seg.getEstadoEnvio().getIdEstadoEnvio())
                    .nombre(seg.getEstadoEnvio().getNombre())
                    .descripcion(seg.getEstadoEnvio().getDescripcion())
                    .build();
        }

        return SeguimientoResponse.builder()
                .idSeguimiento(seg.getIdSeguimiento())
                .estadoEnvio(estadoDto)
                .observacion(seg.getObservacion())
                .createdAt(seg.getCreatedAt() != null ? seg.getCreatedAt().toString() : null)
                .build();
    }
}
