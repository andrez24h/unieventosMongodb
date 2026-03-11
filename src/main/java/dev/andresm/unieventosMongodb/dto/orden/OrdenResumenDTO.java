package dev.andresm.unieventosMongodb.dto.orden;


import dev.andresm.unieventosMongodb.documentos.EstadoCuenta;
import dev.andresm.unieventosMongodb.documentos.Rol;

import java.time.LocalDateTime;

/**
 * DTO de proyección utilizado para listar órdenes de un cliente.

 * Se construye mediante Aggregation en MongoDB y contiene
 * información resumida de la orden junto con datos básicos
 * de la cuenta asociada.

 * Este DTO NO representa el documento completo Orden.
 *
 * @param idOrden Identificador único de la orden.
 * @param fecha Fecha en la que se realizó la orden.
 * @param total Valor total pagado.
 * @param estadoPago Estado actual del pago.
 * @param email Correo electrónico de la cuenta asociada.
 * @param rol Rol de la cuenta (CLIENTE, ADMIN, etc.).
 * @param estadoCuenta Estado actual de la cuenta.
 */
public record OrdenResumenDTO(
        String idOrden,
        LocalDateTime fecha,
        double total,
        String estadoPago,
        String email,
        Rol rol,
        EstadoCuenta estadoCuenta
) {}
