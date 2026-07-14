package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "estados_venta")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EstadoVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_venta")
    private Long idEstadoVenta;

    @Column(unique = true, nullable = false, length = 50)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @OneToMany(mappedBy = "estadoVenta", fetch = FetchType.LAZY)
    private Set<Venta> ventas;
}
