package dev.andresm.unieventosMongodb.dto.cupon;

import dev.andresm.unieventosMongodb.documentos.EstadoCupon;

import java.time.LocalDateTime;
import java.util.List;

public record ActualizarCuponDTO(
        String id,
        String nombre,
        double descuento,
        LocalDateTime fechaVencimiento,
        List<String> beneficiarios,
        EstadoCupon estadoCupon
) {
}
