package dev.andresm.unieventosMongodb.dto.evento;

import java.time.LocalDateTime;

public record FiltrarPorFechaDTO(
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin
) {
}
