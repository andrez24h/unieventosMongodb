package dev.andresm.unieventosMongodb.dto.cuenta;

import lombok.Builder;

@Builder
public record EditarEventoCarritoDTO(
        String idCliente,
        String idDetalle,
        String nuevaLocalidad,
        int nuevaCantidad
) {}
