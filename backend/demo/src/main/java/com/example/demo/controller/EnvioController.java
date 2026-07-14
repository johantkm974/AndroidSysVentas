package com.example.demo.controller;

import com.example.demo.dto.ActualizarEstadoEnvioRequest;
import com.example.demo.dto.EnvioRequest;
import com.example.demo.model.Envio;
import com.example.demo.model.SeguimientoEnvio;
import com.example.demo.service.EnvioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/envios")
@RequiredArgsConstructor
public class EnvioController {

    private final EnvioService envioService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<Envio>> listarTodos() {
        return ResponseEntity.ok(envioService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<Envio> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(envioService.obtenerPorId(id));
    }

    @GetMapping("/pedido/{idPedido}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'CLIENTE')")
    public ResponseEntity<Envio> obtenerPorPedido(@PathVariable Long idPedido) {
        return ResponseEntity.ok(envioService.obtenerPorPedido(idPedido));
    }

    @GetMapping("/{id}/tracking")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'CLIENTE')")
    public ResponseEntity<List<SeguimientoEnvio>> tracking(@PathVariable Long id) {
        return ResponseEntity.ok(envioService.obtenerTracking(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<Envio> crear(@Valid @RequestBody EnvioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(envioService.crear(request));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<Envio> actualizarEstado(@PathVariable Long id,
                                                    @Valid @RequestBody ActualizarEstadoEnvioRequest request) {
        return ResponseEntity.ok(envioService.actualizarEstado(id, request.getIdEstadoEnvio(), request.getObservacion()));
    }
}
