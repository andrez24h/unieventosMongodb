package dev.andresm.unieventosMongodb.dto.evento;

import dev.andresm.unieventosMongodb.documentos.TipoEvento;

import java.time.LocalDateTime;

public record FiltrarPorFechaYTipoYCiudadDTO(
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin,
        TipoEvento tipo,
        String ciudad
) {
}
