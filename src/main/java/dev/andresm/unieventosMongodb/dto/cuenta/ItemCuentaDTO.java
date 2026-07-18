package dev.andresm.unieventosMongodb.dto.cuenta;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record ItemCuentaDTO  (

        String id,
        String email,
        String nombre,
        List<String> telefonos
) {}
