package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "estados_envio")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EstadoEnvio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_envio")
    private Long idEstadoEnvio;

    @Column(unique = true, nullable = false, length = 50)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @OneToMany(mappedBy = "estadoEnvio", fetch = FetchType.LAZY)
    private Set<Envio> envios;

    @OneToMany(mappedBy = "estadoEnvio", fetch = FetchType.LAZY)
    private Set<SeguimientoEnvio> seguimientos;
}
