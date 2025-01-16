package dev.andresm.dto.cuenta;

import lombok.Builder;

import java.util.List;

@Builder
public record InformacionCuentaDTO (

        String id,
        String cedula,
        String direccion,
        String email,
        String nombre,
        List<String> telefono
) {
}
