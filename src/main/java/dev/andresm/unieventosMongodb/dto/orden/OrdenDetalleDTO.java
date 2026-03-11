package dev.andresm.unieventosMongodb.dto.orden;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO utilizado para retornar el detalle completo de una orden.

 * Contiene la información general de la orden y la lista de
 * entradas compradas por el cliente.

 * Cada ítem incluye información de:
 * - evento
 * - localidad
 * - cantidad
 * - precio
 * - código QR asociado a la entrada

 * Este DTO es utilizado cuando el cliente consulta el detalle
 * de su compra o cuando el sistema necesita mostrar las
 * entradas adquiridas.
 */
public record OrdenDetalleDTO(
        String idOrden,
        String idCliente,
        LocalDateTime fecha,
        double total,
        String estadoPago,
        List<ItemOrdenDetalleDTO> items
) {}
