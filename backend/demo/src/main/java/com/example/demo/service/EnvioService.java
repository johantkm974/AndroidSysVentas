package com.example.demo.service;

import com.example.demo.dto.EnvioRequest;
import com.example.demo.model.Envio;
import com.example.demo.model.SeguimientoEnvio;

import java.util.List;

public interface EnvioService {
    List<Envio> listarTodos();
    Envio obtenerPorId(Long id);
    Envio obtenerPorPedido(Long idPedido);
    Envio crear(EnvioRequest request);
    Envio actualizarEstado(Long idEnvio, Long idEstadoEnvio, String observacion);
    List<SeguimientoEnvio> obtenerTracking(Long idEnvio);
}
