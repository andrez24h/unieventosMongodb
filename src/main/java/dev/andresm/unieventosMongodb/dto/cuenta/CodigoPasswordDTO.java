package dev.andresm.unieventosMongodb.dto.cuenta;

import jakarta.validation.constraints.NotBlank;

public record CodigoPasswordDTO(
        @NotBlank String email
) {
}
