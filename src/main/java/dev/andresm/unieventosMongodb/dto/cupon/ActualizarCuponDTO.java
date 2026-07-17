package dev.andresm.unieventosMongodb.dto.cupon;

import dev.andresm.unieventosMongodb.documentos.EstadoCupon;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;
import java.util.List;

public record ActualizarCuponDTO(

        @NotBlank String id,
        @NotBlank String nombre,
        @Positive double descuento,
        @NotNull LocalDateTime fechaVencimiento,
        List<String> beneficiarios,
        @NotNull EstadoCupon estadoCupon
) {}
