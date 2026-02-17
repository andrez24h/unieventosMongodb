package dev.andresm.unieventosMongodb.dto.evento;

import dev.andresm.unieventosMongodb.documentos.TipoEvento;

import java.time.LocalDate;

public record FiltrarPorFechaYTipoDTO(
        LocalDate fecha,
        TipoEvento tipo
) {
}
