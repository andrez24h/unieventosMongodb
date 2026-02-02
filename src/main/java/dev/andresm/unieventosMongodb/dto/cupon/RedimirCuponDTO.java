package dev.andresm.unieventosMongodb.dto.cupon;

import jakarta.validation.constraints.NotBlank;

public record RedimirCuponDTO(
        @NotBlank String codigoCupon,
        @NotBlank String idCliente

) {
}
