package com.example.demo.controller;

import com.example.demo.dto.VentaRequest;
import com.example.demo.dto.VentaResponse;
import com.example.demo.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService ventaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<VentaResponse>> listarTodas() {
        return ResponseEntity.ok(ventaService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<VentaResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.obtenerPorId(id));
    }

    @PostMapping("/procesar")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<VentaResponse> procesarVenta(@Valid @RequestBody VentaRequest request,
                                                         Authentication authentication) {
        Long idUsuarioVendedor = (Long) authentication.getCredentials();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ventaService.procesarVenta(request, idUsuarioVendedor));
    }

    @PatchMapping("/{id}/anular")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<VentaResponse> anular(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.anularVenta(id));
    }
}
