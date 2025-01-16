package dev.andresm.dto.cuenta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record ActualizarCuentaDTO (

        @NotBlank @Length(max = 12) String id,
        @NotBlank @Length(max = 10) String cedula,  // La cédula no puede estar vacía y tiene un máximo de 10 caracteres
        @NotBlank @Size(max = 100) String direccion,  // La dirección es obligatoria y no puede exceder 100 caracteres
        @NotBlank @Size(max = 100) String nombre,      // El nombre es obligatorio y no puede exceder 100 caracteres
        @Size(min = 1) List<String> telefonos          // Lista de teléfonos con al menos 1 número
) {
}

