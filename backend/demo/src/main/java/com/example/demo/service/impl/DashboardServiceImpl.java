package com.example.demo.service.impl;

import com.example.demo.dto.DashboardResponse;
import com.example.demo.model.EstadoEnvio;
import com.example.demo.model.EstadoVenta;
import com.example.demo.model.Venta;
import com.example.demo.repository.*;
import com.example.demo.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final VentaRepository ventaRepository;
    private final EnvioRepository envioRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final EstadoEnvioRepository estadoEnvioRepository;

    @Override
    public DashboardResponse obtenerResumen() {
        long totalProductos = productoRepository.count();
        long totalUsuarios = usuarioRepository.count();

        long totalPedidosPendientes = estadoPedidoRepository.findByNombre("CONFIRMADO")
                .map(e -> (long) pedidoRepository.findByEstadoPedidoIdEstadoPedido(e.getIdEstadoPedido()).size())
                .orElse(0L);

        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finMes = inicioMes.plusMonths(1);

        List<Venta> ventasDelMes = ventaRepository.findAll().stream()
                .filter(v -> v.getCreatedAt() != null
                        && !v.getCreatedAt().isBefore(inicioMes)
                        && v.getCreatedAt().isBefore(finMes))
                .collect(Collectors.toList());

        long totalVentasDelMes = ventasDelMes.size();
        BigDecimal ingresosDelMes = ventasDelMes.stream()
                .map(v -> v.getTotal() != null ? v.getTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalEnviosPendientes = estadoEnvioRepository.findByNombre("PENDIENTE")
                .map(ee -> (long) envioRepository.findByEstadoEnvioIdEstadoEnvio(ee.getIdEstadoEnvio()).size())
                .orElse(0L);

        List<DashboardResponse.ProductoStockBajo> stockBajo = productoRepository
                .findByStockLessThanEqual(5).stream()
                .map(p -> DashboardResponse.ProductoStockBajo.builder()
                        .idProducto(p.getIdProducto())
                        .nombre(p.getNombre())
                        .codigo(p.getCodigo())
                        .stock(p.getStock())
                        .stockMinimo(p.getStockMinimo())
                        .build())
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .resumen(DashboardResponse.ResumenGeneral.builder()
                        .totalProductos(totalProductos)
                        .totalUsuarios(totalUsuarios)
                        .totalPedidosPendientes(totalPedidosPendientes)
                        .totalVentasDelMes(totalVentasDelMes)
                        .ingresosDelMes(ingresosDelMes)
                        .totalEnviosPendientes(totalEnviosPendientes)
                        .build())
                .productosMasVendidos(List.of())
                .productosStockBajo(stockBajo)
                .build();
    }
}
