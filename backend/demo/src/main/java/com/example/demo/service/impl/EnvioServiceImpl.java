package com.example.demo.service.impl;

import com.example.demo.dto.EnvioRequest;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.EnvioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnvioServiceImpl implements EnvioService {

    private final EnvioRepository envioRepository;
    private final PedidoRepository pedidoRepository;
    private final EstadoEnvioRepository estadoEnvioRepository;
    private final SeguimientoEnvioRepository seguimientoRepository;

    @Override
    public List<Envio> listarTodos() {
        return envioRepository.findAll();
    }

    @Override
    public Envio obtenerPorId(Long id) {
        return envioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado: " + id));
    }

    @Override
    public Envio obtenerPorPedido(Long idPedido) {
        return envioRepository.findByPedidoIdPedido(idPedido)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado para el pedido: " + idPedido));
    }

    @Override
    @Transactional
    public Envio crear(EnvioRequest request) {
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

        return envio;
    }

    @Override
    @Transactional
    public Envio actualizarEstado(Long idEnvio, Long idEstadoEnvio, String observacion) {
        Envio envio = envioRepository.findById(idEnvio)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado: " + idEnvio));

        EstadoEnvio nuevoEstado = estadoEnvioRepository.findById(idEstadoEnvio)
                .orElseThrow(() -> new ResourceNotFoundException("Estado de envío no encontrado: " + idEstadoEnvio));

        envio.setEstadoEnvio(nuevoEstado);

        if ("EN_RUTA".equals(nuevoEstado.getNombre())) {
            envio.setFechaEnvio(java.time.LocalDateTime.now());
        }
        if ("ENTREGADO".equals(nuevoEstado.getNombre())) {
            envio.setFechaEntrega(java.time.LocalDateTime.now());
        }

        SeguimientoEnvio seguimiento = SeguimientoEnvio.builder()
                .envio(envio)
                .estadoEnvio(nuevoEstado)
                .observacion(observacion)
                .build();
        seguimientoRepository.save(seguimiento);

        return envioRepository.save(envio);
    }

    @Override
    public List<SeguimientoEnvio> obtenerTracking(Long idEnvio) {
        return seguimientoRepository.findByEnvioIdEnvioOrderByCreatedAtDesc(idEnvio);
    }
}
