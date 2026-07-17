package dev.andresm.unieventosMongodb.dto.evento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record FiltrarPorFechaYCiudadDTO(

        @NotNull LocalDate fecha,
        @NotBlank String ciudad
) {}
