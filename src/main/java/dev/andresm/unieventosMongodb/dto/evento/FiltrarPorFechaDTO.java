package dev.andresm.unieventosMongodb.dto.evento;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record FiltrarPorFechaDTO(
        LocalDate fecha
) {
}
