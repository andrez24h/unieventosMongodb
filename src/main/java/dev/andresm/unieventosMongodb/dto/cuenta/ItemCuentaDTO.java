package dev.andresm.unieventosMongodb.dto.cuenta;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record ItemCuentaDTO  (

        @NotBlank String id,
        @NotBlank String email,
        @NotBlank String nombre,
        List<String> telefonos
) {
}
