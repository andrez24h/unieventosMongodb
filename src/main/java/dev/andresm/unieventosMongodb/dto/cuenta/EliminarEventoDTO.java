package dev.andresm.unieventosMongodb.dto.cuenta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EliminarEventoDTO(
        @NotBlank String idCliente,
        @NotNull String idDetalle
) {
}
