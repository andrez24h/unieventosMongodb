package dev.andresm.unieventosMongodb.dto.cupon;

import dev.andresm.unieventosMongodb.documentos.TipoCupon;

import java.time.LocalDateTime;

public record ItemCuponDTO(
        String id,
        String codigo,
        String nombre,
        double descuento,
        LocalDateTime fechaVencimiento,
        TipoCupon tipoCupon
) {
}
