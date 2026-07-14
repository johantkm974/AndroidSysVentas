package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "estados_pedido")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EstadoPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado_pedido")
    private Long idEstadoPedido;

    @Column(unique = true, nullable = false, length = 50)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @OneToMany(mappedBy = "estadoPedido", fetch = FetchType.LAZY)
    private Set<Pedido> pedidos;
}
