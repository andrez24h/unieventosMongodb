package dev.andresm.unieventosMongodb.dto.cupon;

import dev.andresm.unieventosMongodb.documentos.TipoCupon;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CrearCuponDTO(
        @NotBlank String codigo,
        @NotBlank String nombre,
        @NotNull double descuento,
        LocalDateTime fechaVencimiento,
        @NotNull TipoCupon tipo,
        List<String> beneficiarios
) {
}
