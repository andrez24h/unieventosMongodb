package dev.andresm.unieventosMongodb.dto.evento;

public record DisponibilidadEventoDTO(
        String idEvento,
        String idLocalidad,
        int cantidad
) {
}
