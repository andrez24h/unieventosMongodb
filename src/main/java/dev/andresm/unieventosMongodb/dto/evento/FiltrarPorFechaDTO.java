package dev.andresm.unieventosMongodb.dto.evento;

import java.time.LocalDate;

public record FiltrarPorFechaDTO(
        LocalDate fecha
) {
}
