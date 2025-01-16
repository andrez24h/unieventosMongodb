package dev.andresm.dto.cuenta;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Builder
public record CrearCuentaDTO (

        @NotBlank @Length(max = 10) String cedula,  // La cédula no puede estar vacía y tiene un máximo de 10 caracteres
        @NotBlank @Length(max = 100) String direccion, // Dirección no puede estar vacía y tiene un máximo de 100 caracteres
        @NotBlank @Length(max = 50) @Email String email, // Email no puede estar vacío y debe ser válido
        @NotBlank @Length(max = 100) String nombre, // Nombre no puede estar vacío y tiene un máximo de 100 caracteres
        @NotBlank @Length(min = 7, max = 20) String password, // Contraseña debe tener entre 7 y 20 caracteres
        @NotBlank @Size(min = 1) List<String> telefonos // La lista de teléfonos no puede estar vacía, aunque la longitud de cada teléfono puede estar controlada en otro lugar
) {
}