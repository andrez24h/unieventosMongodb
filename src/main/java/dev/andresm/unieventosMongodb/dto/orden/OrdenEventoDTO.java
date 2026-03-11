package dev.andresm.unieventosMongodb.dto.orden;

import java.time.LocalDateTime;

/**
 * DTO de proyección utilizado para listar órdenes que contienen
 * un evento específico dentro de sus ítems.

 * Se utiliza principalmente para reportes administrativos.
 *
 * @param idOrden Identificador de la orden.
 * @param idCliente Identificador del cliente que realizó la compra.
 * @param fecha Fecha de creación de la orden.
 * @param total Valor total pagado.
 */
public record OrdenEventoDTO(
        String idOrden,
        String idCliente,
        LocalDateTime fecha,
        double total
) {}
