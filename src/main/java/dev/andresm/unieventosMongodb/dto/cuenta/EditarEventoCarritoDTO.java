package dev.andresm.unieventosMongodb.dto.cuenta;

public record EditarEventoCarritoDTO(
        String idCliente,
        String idDetalle,
        String nuevaLocalidad,
        int nuevaCantidad
) {
}
