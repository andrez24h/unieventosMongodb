package dev.andresm.unieventosMongodb.dto.orden;

/**
 * DTO que representa un ítem dentro del detalle de una orden.

 * Este DTO se utiliza para retornar la información de cada
 * entrada comprada dentro de una orden junto con su código QR.

 * NOTA:
 * El QR se envía en formato Base64 para que el frontend
 * pueda renderizarlo directamente como imagen.
 */
public record ItemOrdenDetalleDTO(
        String idEvento,
        int cantidad,
        double precioUnitario,
        String nombreLocalidad,

        // Código QR en formato Base64
        String qr
) {}
