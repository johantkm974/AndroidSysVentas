package com.example.demo.service.impl;

import com.example.demo.dto.ProductoRequest;
import com.example.demo.dto.ProductoResponse;
import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final ProveedorRepository proveedorRepository;

    @Override
    public List<ProductoResponse> listarTodos() {
        return productoRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoResponse obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
        return toResponse(producto);
    }

    @Override
    public ProductoResponse obtenerPorCodigo(String codigo) {
        Producto producto = productoRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + codigo));
        return toResponse(producto);
    }

    @Override
    public ProductoResponse crear(ProductoRequest request) {
        if (productoRepository.findByCodigo(request.getCodigo()).isPresent()) {
            throw new BadRequestException("El código ya existe");
        }
        if (request.getIdCategoria() == null) throw new BadRequestException("idCategoria es obligatorio");
        if (request.getIdMarca() == null) throw new BadRequestException("idMarca es obligatorio");
        if (request.getIdProveedor() == null) throw new BadRequestException("idProveedor es obligatorio");

        Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        Marca marca = marcaRepository.findById(request.getIdMarca())
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada"));
        Proveedor proveedor = proveedorRepository.findById(request.getIdProveedor())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));

        Producto producto = Producto.builder()
                .codigo(request.getCodigo())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .precioCompra(request.getPrecioCompra())
                .precioVenta(request.getPrecioVenta())
                .stock(request.getStock() != null ? request.getStock() : 0)
                .stockMinimo(request.getStockMinimo() != null ? request.getStockMinimo() : 5)
                .imagen(request.getImagen())
                .categoria(categoria)
                .marca(marca)
                .proveedor(proveedor)
                .activo(true)
                .build();

        return toResponse(productoRepository.save(producto));
    }

    @Override
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));

        if (request.getIdCategoria() != null) {
            Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
            producto.setCategoria(categoria);
        }
        if (request.getIdMarca() != null) {
            Marca marca = marcaRepository.findById(request.getIdMarca())
                    .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada"));
            producto.setMarca(marca);
        }
        if (request.getIdProveedor() != null) {
            Proveedor proveedor = proveedorRepository.findById(request.getIdProveedor())
                    .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado"));
            producto.setProveedor(proveedor);
        }

        producto.setCodigo(request.getCodigo());
        producto.setNombre(request.getNombre());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecioCompra(request.getPrecioCompra());
        producto.setPrecioVenta(request.getPrecioVenta());
        producto.setStock(request.getStock() != null ? request.getStock() : producto.getStock());
        producto.setStockMinimo(request.getStockMinimo() != null ? request.getStockMinimo() : producto.getStockMinimo());
        producto.setImagen(request.getImagen());

        return toResponse(productoRepository.save(producto));
    }

    @Override
    public void eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado: " + id));
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Override
    public List<ProductoResponse> buscarPorCategoria(Long idCategoria) {
        return productoRepository.findByCategoriaIdCategoria(idCategoria).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponse> buscarPorMarca(Long idMarca) {
        return productoRepository.findByMarcaIdMarca(idMarca).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponse> buscarPorProveedor(Long idProveedor) {
        return productoRepository.findByProveedorIdProveedor(idProveedor).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponse> productosConStockBajo() {
        return productoRepository.findByStockLessThanEqual(5).stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    private ProductoResponse toResponse(Producto p) {
        return ProductoResponse.builder()
                .idProducto(p.getIdProducto())
                .codigo(p.getCodigo())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .precioCompra(p.getPrecioCompra())
                .precioVenta(p.getPrecioVenta())
                .stock(p.getStock())
                .stockMinimo(p.getStockMinimo())
                .imagen(p.getImagen())
                .categoria(p.getCategoria().getNombre())
                .marca(p.getMarca().getNombre())
                .proveedor(p.getProveedor().getRazonSocial())
                .activo(p.getActivo())
                .createdAt(p.getCreatedAt())
                .build();
    }
}
