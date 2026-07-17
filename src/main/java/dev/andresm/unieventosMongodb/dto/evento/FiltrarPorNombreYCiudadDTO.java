package dev.andresm.unieventosMongodb.dto.evento;

import jakarta.validation.constraints.NotBlank;

public record FiltrarPorNombreYCiudadDTO(

        @NotBlank String nombre,
        @NotBlank String ciudad
) {}
