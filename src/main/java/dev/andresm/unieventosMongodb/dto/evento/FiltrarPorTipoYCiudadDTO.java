package dev.andresm.unieventosMongodb.dto.evento;

import dev.andresm.unieventosMongodb.documentos.TipoEvento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FiltrarPorTipoYCiudadDTO(

        @NotNull TipoEvento tipo,
        @NotBlank String ciudad
) {}
