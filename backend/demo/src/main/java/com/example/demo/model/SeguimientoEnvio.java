package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "seguimiento_envio")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SeguimientoEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_seguimiento")
    private Long idSeguimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_envio", nullable = false)
    private Envio envio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_estado_envio", nullable = false)
    private EstadoEnvio estadoEnvio;

    @Column(length = 255)
    private String observacion;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
