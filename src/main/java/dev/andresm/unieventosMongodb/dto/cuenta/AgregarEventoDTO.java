package dev.andresm.unieventosMongodb.dto.cuenta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public record AgregarEventoDTO(
        @NotNull int cantidad,
        @NotBlank String nombreLocalidad,
        @NotNull String idEvento,
        @NotBlank String idUsuario,
        @NotNull LocalDateTime fecha
) {
}
