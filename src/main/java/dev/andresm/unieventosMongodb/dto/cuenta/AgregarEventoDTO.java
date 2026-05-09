package dev.andresm.unieventosMongodb.dto.cuenta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record AgregarEventoDTO(
        @NotNull int cantidad,
        @NotBlank String nombreLocalidad,
        @NotNull String idEvento,
        @NotBlank String idUsuario,
        @NotNull LocalDateTime fecha
) {
}
