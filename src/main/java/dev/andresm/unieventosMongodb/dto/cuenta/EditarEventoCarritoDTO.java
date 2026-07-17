package dev.andresm.unieventosMongodb.dto.cuenta;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EditarEventoCarritoDTO(

        @NotBlank String idCliente,
        @NotBlank String idDetalle,
        @NotBlank String nuevaLocalidad,
        @Min(1) int nuevaCantidad
) {}
