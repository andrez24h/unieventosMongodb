package dev.andresm.unieventosMongodb.dto.evento;

import dev.andresm.unieventosMongodb.documentos.TipoEvento;

public record FiltroEventoDTO(
        String nombre,
        TipoEvento tipo,
        String ciudad
) {
}
