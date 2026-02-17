package dev.andresm.unieventosMongodb.dto.evento;

import dev.andresm.unieventosMongodb.documentos.TipoEvento;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record FiltrarPorFechaYTipoYCiudadDTO(
        LocalDate fecha,
        TipoEvento tipo,
        String ciudad
) {
}
