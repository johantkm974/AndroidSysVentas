package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.MovimientoInventario;
import com.example.demo.model.Producto;
import com.example.demo.model.Usuario;
import com.example.demo.repository.MovimientoInventarioRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.MovimientoInventarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioServiceImpl implements MovimientoInventarioService {

    private final MovimientoInventarioRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    public List<MovimientoInventario> listarTodos() {
        return movimientoRepository.findAll();
    }

    @Override
    public MovimientoInventario obtenerPorId(Long id) {
        return movimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado: " + id));
    }

    @Override
    public List<MovimientoInventario> listarPorProducto(Long idProducto) {
        return movimientoRepository.findByProductoIdProductoOrderByCreatedAtDesc(idProducto);
    }

    @Override
    @Transactional
    public MovimientoInventario registrarEntrada(Long idProducto, Long idUsuario, Integer cantidad, String descripcion) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        int stockAnterior = producto.getStock();
        producto.setStock(stockAnterior + cantidad);
        productoRepository.save(producto);

        MovimientoInventario movimiento = MovimientoInventario.builder()
                .producto(producto)
                .usuario(usuario)
                .tipoMovimiento(MovimientoInventario.TipoMovimiento.ENTRADA)
                .cantidad(cantidad)
                .stockAnterior(stockAnterior)
                .stockActual(producto.getStock())
                .descripcion(descripcion)
                .build();

        return movimientoRepository.save(movimiento);
    }

    @Override
    @Transactional
    public MovimientoInventario registrarSalida(Long idProducto, Long idUsuario, Integer cantidad, String descripcion) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (producto.getStock() < cantidad) {
            throw new BadRequestException("Stock insuficiente para el producto: " + producto.getNombre());
        }

        int stockAnterior = producto.getStock();
        producto.setStock(stockAnterior - cantidad);
        productoRepository.save(producto);

        MovimientoInventario movimiento = MovimientoInventario.builder()
                .producto(producto)
                .usuario(usuario)
                .tipoMovimiento(MovimientoInventario.TipoMovimiento.SALIDA)
                .cantidad(cantidad)
                .stockAnterior(stockAnterior)
                .stockActual(producto.getStock())
                .descripcion(descripcion)
                .build();

        return movimientoRepository.save(movimiento);
    }

    @Override
    @Transactional
    public MovimientoInventario registrarAjuste(Long idProducto, Long idUsuario, Integer nuevoStock, String descripcion) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        int stockAnterior = producto.getStock();
        producto.setStock(nuevoStock);
        productoRepository.save(producto);

        MovimientoInventario movimiento = MovimientoInventario.builder()
                .producto(producto)
                .usuario(usuario)
                .tipoMovimiento(MovimientoInventario.TipoMovimiento.AJUSTE)
                .cantidad(nuevoStock - stockAnterior)
                .stockAnterior(stockAnterior)
                .stockActual(nuevoStock)
                .descripcion(descripcion)
                .build();

        return movimientoRepository.save(movimiento);
    }
}
