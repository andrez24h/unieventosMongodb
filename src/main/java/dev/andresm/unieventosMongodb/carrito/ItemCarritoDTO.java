package dev.andresm.unieventosMongodb.carrito;

import lombok.Builder;

/**
 * DTO que representa un ítem dentro del carrito de compras.

 * Contiene la información necesaria para mostrar:
 * - Evento asociado.
 * - Localidad seleccionada.
 * - Cantidad de entradas.
 * - Precio unitario.
 * - Subtotal calculado.
 */
@Builder
public record ItemCarritoDTO(
        String idEvento,
        String nombreEvento,
        String nombreLocalidad,
        int cantidad,
        double precioUnitario,
        double subtotal
) {
}
