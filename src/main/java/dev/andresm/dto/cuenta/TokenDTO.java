package dev.andresm.dto.cuenta;

import jakarta.validation.constraints.NotBlank;

public record TokenDTO (

        @NotBlank String token
) {
}
