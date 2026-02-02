package dev.andresm.unieventosMongodb.dto.cupon;

import jakarta.validation.constraints.NotBlank;

public record ListarCuponDTO(
        @NotBlank String idCliente
) {
}
