package dev.andresm.unieventosMongodb.dto.evento;

import dev.andresm.unieventosMongodb.documentos.TipoEvento;

public record FiltrarPorTipoYCiudadDTO(
        TipoEvento tipo,
        String ciudad
) {
}
