package dev.andresm.unieventosMongodb.dto.evento;

import java.time.LocalDate;

public record FiltrarPorFechaYCiudadDTO(
        LocalDate fecha,
        String ciudad
) {
}
