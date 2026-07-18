package com.example.demo.controller;

import com.example.demo.dto.ActualizarEstadoEnvioRequest;
import com.example.demo.dto.EnvioRequest;
import com.example.demo.dto.EnvioResponse;
import com.example.demo.dto.SeguimientoResponse;
import com.example.demo.service.EnvioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/envios")
@RequiredArgsConstructor
public class EnvioController {

    private final EnvioService envioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<EnvioResponse>> listarTodos() {
        return ResponseEntity.ok(envioService.listarTodos());
    }

    @GetMapping("/mis-envios")
    @PreAuthorize("hasRole('REPARTIDOR')")
    public ResponseEntity<List<EnvioResponse>> listarMisEnvios(Authentication auth) {
        Long idRepartidor = (Long) auth.getCredentials();
        return ResponseEntity.ok(envioService.listarPorRepartidor(idRepartidor));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'REPARTIDOR')")
    public ResponseEntity<EnvioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(envioService.obtenerPorId(id));
    }

    @GetMapping("/pedido/{idPedido}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'CLIENTE', 'REPARTIDOR')")
    public ResponseEntity<EnvioResponse> obtenerPorPedido(@PathVariable Long idPedido) {
        return ResponseEntity.ok(envioService.obtenerPorPedido(idPedido));
    }

    @GetMapping("/{id}/tracking")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'CLIENTE', 'REPARTIDOR')")
    public ResponseEntity<List<SeguimientoResponse>> tracking(@PathVariable Long id) {
        return ResponseEntity.ok(envioService.obtenerTracking(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<EnvioResponse> crear(@Valid @RequestBody EnvioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(envioService.crear(request));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'REPARTIDOR')")
    public ResponseEntity<EnvioResponse> actualizarEstado(@PathVariable Long id,
                                                    @Valid @RequestBody ActualizarEstadoEnvioRequest request) {
        return ResponseEntity.ok(envioService.actualizarEstado(id, request.getIdEstadoEnvio(), request.getObservacion()));
    }

    @PatchMapping("/{id}/asignar-repartidor/{idRepartidor}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EnvioResponse> asignarRepartidor(@PathVariable Long id, @PathVariable Long idRepartidor) {
        return ResponseEntity.ok(envioService.asignarRepartidor(id, idRepartidor));
    }
}
