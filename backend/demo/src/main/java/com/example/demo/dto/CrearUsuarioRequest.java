package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CrearUsuarioRequest {
    @NotBlank @Size(max = 100)
    private String nombres;
    @NotBlank @Size(max = 100)
    private String apellidos;
    @NotBlank @Size(min = 8, max = 20)
    private String dni;
    private String telefono;
    private String direccion;
    @NotBlank @Email @Size(max = 150)
    private String correo;
    @NotBlank @Size(min = 6)
    private String password;
    @NotEmpty
    private List<Long> idRoles;
}
