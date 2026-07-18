package com.example.demo.service;

import com.example.demo.dto.ActualizarEstadoEnvioRequest;
import com.example.demo.dto.EnvioRequest;
import com.example.demo.dto.EnvioResponse;
import com.example.demo.dto.SeguimientoResponse;

import java.util.List;

public interface EnvioService {
    List<EnvioResponse> listarTodos();
    EnvioResponse obtenerPorId(Long id);
    EnvioResponse obtenerPorPedido(Long idPedido);
    EnvioResponse crear(EnvioRequest request);
    EnvioResponse actualizarEstado(Long idEnvio, Long idEstadoEnvio, String observacion);
    List<SeguimientoResponse> obtenerTracking(Long idEnvio);
    List<EnvioResponse> listarPorRepartidor(Long idRepartidor);
    EnvioResponse asignarRepartidor(Long idEnvio, Long idRepartidor);
}
