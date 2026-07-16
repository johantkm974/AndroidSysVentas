package com.example.demo.repository;

import com.example.demo.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByCodigo(String codigo);
    List<Producto> findByCategoriaIdCategoria(Long idCategoria);
    List<Producto> findByMarcaIdMarca(Long idMarca);
    List<Producto> findByProveedorIdProveedor(Long idProveedor);
    List<Producto> findByStockLessThanEqual(Integer stockMinimo);
    List<Producto> findByActivoTrue();
    long countByCategoriaIdCategoria(Long idCategoria);
    long countByMarcaIdMarca(Long idMarca);
    long countByProveedorIdProveedor(Long idProveedor);
}
