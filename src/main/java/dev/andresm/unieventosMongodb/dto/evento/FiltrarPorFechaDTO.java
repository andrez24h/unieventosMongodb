package dev.andresm.unieventosMongodb.dto.evento;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record FiltrarPorFechaDTO(

        @NotNull LocalDate fecha
) {}
