package dev.andresm.unieventosMongodb.dto.evento;

import dev.andresm.unieventosMongodb.documentos.TipoEvento;

import java.time.LocalDate;

public record FiltrarPorFechaYTipoDTO(
        LocalDate fechaInicio,
        LocalDate fechaFin,
        TipoEvento tipo
) {
}
