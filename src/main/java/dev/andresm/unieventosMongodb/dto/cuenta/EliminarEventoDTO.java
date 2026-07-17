package dev.andresm.unieventosMongodb.dto.cuenta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record EliminarEventoDTO(

        @NotBlank String idCliente,
        @NotNull String idDetalle
) {}
