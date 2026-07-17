package dev.andresm.unieventosMongodb.dto.cuenta;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AgregarEventoDTO(

        @Min(1) int cantidad,
        @NotBlank String nombreLocalidad,
        @NotNull String idEvento,
        @NotBlank String idUsuario,
        @NotNull LocalDateTime fecha
) {}
