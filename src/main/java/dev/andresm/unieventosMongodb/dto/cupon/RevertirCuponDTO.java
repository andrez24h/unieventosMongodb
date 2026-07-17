package dev.andresm.unieventosMongodb.dto.cupon;

import jakarta.validation.constraints.NotBlank;

public record RevertirCuponDTO(

        @NotBlank String codigoCupon,
        @NotBlank String idCliente
) {}
