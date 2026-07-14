package com.example.demo.service;

import com.example.demo.dto.VentaRequest;
import com.example.demo.dto.VentaResponse;
import java.util.List;

public interface VentaService {
    List<VentaResponse> listarTodas();
    VentaResponse obtenerPorId(Long id);
    VentaResponse procesarVenta(VentaRequest request, Long idUsuarioVendedor);
    VentaResponse anularVenta(Long idVenta);
}
