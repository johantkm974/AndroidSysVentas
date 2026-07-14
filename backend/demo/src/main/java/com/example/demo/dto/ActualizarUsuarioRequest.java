package com.example.demo.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ActualizarUsuarioRequest {
    @Size(max = 100)
    private String nombres;
    @Size(max = 100)
    private String apellidos;
    @Size(min = 8, max = 20)
    private String dni;
    private String telefono;
    private String direccion;
    private Boolean activo;
    private List<Long> idRoles;
}
