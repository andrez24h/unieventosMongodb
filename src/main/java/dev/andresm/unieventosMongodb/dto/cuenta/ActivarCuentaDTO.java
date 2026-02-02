package dev.andresm.unieventosMongodb.dto.cuenta;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ActivarCuentaDTO(
        @NotBlank @Email String email,
        @NotBlank String codigoVerificacion
) {
}
