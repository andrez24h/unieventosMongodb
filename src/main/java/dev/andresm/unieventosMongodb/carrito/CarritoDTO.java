package dev.andresm.unieventosMongodb.carrito;


import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que representa la información completa del carrito de una cuenta.

 * Incluye:
 * - Total general calculado.
 * - Fecha de creación del carrito.
 * - Lista de ítems agregados.
 */
public record CarritoDTO(
        double total,
        LocalDateTime fecha,
        List<ItemCarritoDTO> items
) {
}
