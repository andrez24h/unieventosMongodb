package dev.andresm.unieventosMongodb.dto.evento;

import dev.andresm.unieventosMongodb.documentos.TipoEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record FiltrarPorFechaYTipoYCiudadDTO(

        @NotNull LocalDate fecha,
        @NotNull TipoEvento tipo,
        @NotBlank String ciudad
) {}
