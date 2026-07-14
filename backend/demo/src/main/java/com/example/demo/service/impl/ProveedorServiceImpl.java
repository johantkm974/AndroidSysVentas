package com.example.demo.service.impl;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Proveedor;
import com.example.demo.repository.ProveedorRepository;
import com.example.demo.service.ProveedorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    @Override
    public List<Proveedor> listarTodos() {
        return proveedorRepository.findAll();
    }

    @Override
    public Proveedor obtenerPorId(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado: " + id));
    }

    @Override
    public Proveedor crear(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    @Override
    public Proveedor actualizar(Long id, Proveedor proveedor) {
        Proveedor existente = obtenerPorId(id);
        existente.setRazonSocial(proveedor.getRazonSocial());
        existente.setRuc(proveedor.getRuc());
        existente.setTelefono(proveedor.getTelefono());
        existente.setCorreo(proveedor.getCorreo());
        existente.setDireccion(proveedor.getDireccion());
        existente.setActivo(proveedor.getActivo());
        return proveedorRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        Proveedor proveedor = obtenerPorId(id);
        proveedor.setActivo(false);
        proveedorRepository.save(proveedor);
    }
}
