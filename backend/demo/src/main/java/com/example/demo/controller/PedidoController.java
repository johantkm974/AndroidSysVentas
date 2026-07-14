package com.example.demo.controller;

import com.example.demo.dto.PedidoRequest;
import com.example.demo.dto.PedidoResponse;
import com.example.demo.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {

    private final PedidoService pedidoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<List<PedidoResponse>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR', 'CLIENTE')")
    public ResponseEntity<PedidoResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.obtenerPorId(id));
    }

    @GetMapping("/numero/{numeroPedido}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<PedidoResponse> obtenerPorNumero(@PathVariable String numeroPedido) {
        return ResponseEntity.ok(pedidoService.obtenerPorNumero(numeroPedido));
    }

    @GetMapping("/mis-pedidos")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<PedidoResponse>> misPedidos(Authentication authentication) {
        Long idUsuario = (Long) authentication.getCredentials();
        return ResponseEntity.ok(pedidoService.listarPorCliente(idUsuario));
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PedidoResponse> crear(@Valid @RequestBody PedidoRequest request,
                                                  Authentication authentication) {
        Long idUsuario = (Long) authentication.getCredentials();
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(request, idUsuario));
    }

    @PatchMapping("/{id}/estado/{idEstado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDEDOR')")
    public ResponseEntity<PedidoResponse> cambiarEstado(@PathVariable Long id, @PathVariable Long idEstado) {
        return ResponseEntity.ok(pedidoService.cambiarEstado(id, idEstado));
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PedidoResponse> cancelar(@PathVariable Long id, Authentication authentication) {
        Long idUsuario = (Long) authentication.getCredentials();
        return ResponseEntity.ok(pedidoService.cancelarPorCliente(id, idUsuario));
    }
}
