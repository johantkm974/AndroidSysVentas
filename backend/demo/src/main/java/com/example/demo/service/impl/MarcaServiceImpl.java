package com.example.demo.service.impl;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Marca;
import com.example.demo.repository.MarcaRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.service.MarcaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarcaServiceImpl implements MarcaService {

    private final MarcaRepository marcaRepository;
    private final ProductoRepository productoRepository;

    @Override
    public List<Marca> listarTodas() {
        return marcaRepository.findAll();
    }

    @Override
    public Marca obtenerPorId(Long id) {
        return marcaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marca no encontrada: " + id));
    }

    @Override
    public Marca crear(Marca marca) {
        return marcaRepository.save(marca);
    }

    @Override
    public Marca actualizar(Long id, Marca marca) {
        Marca existente = obtenerPorId(id);
        existente.setNombre(marca.getNombre());
        existente.setDescripcion(marca.getDescripcion());
        existente.setActivo(marca.getActivo());
        return marcaRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        Marca marca = obtenerPorId(id);
        if (productoRepository.countByMarcaIdMarca(id) > 0) {
            throw new BadRequestException(
                    "No se puede desactivar esta marca porque tiene productos asociados.");
        }
        marca.setActivo(false);
        marcaRepository.save(marca);
    }
}
