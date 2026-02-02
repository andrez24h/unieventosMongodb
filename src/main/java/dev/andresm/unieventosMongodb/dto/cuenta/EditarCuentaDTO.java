package dev.andresm.unieventosMongodb.dto.cuenta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record EditarCuentaDTO(
        @NotBlank @Length(max = 12) String id,
        @Length(max = 100) String nombre,
        @NotNull List<String> telefonos,
        @Length(max = 100)String direccion
) {
}
