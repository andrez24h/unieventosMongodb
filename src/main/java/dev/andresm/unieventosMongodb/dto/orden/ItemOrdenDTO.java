package dev.andresm.unieventosMongodb.dto.orden;

import dev.andresm.unieventosMongodb.documentos.DetalleOrden;

import java.time.LocalDateTime;
import java.util.List;

public record ItemOrdenDTO(
        String idCliente,
        String idCupon,
        double total,
        LocalDateTime fecha,
        List<DetalleOrden> items
) {
}
