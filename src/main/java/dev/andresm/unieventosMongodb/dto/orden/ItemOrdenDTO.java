package dev.andresm.unieventosMongodb.dto.orden;

import dev.andresm.unieventosMongodb.documentos.EstadoCuenta;
import dev.andresm.unieventosMongodb.documentos.EstadoOrden;
import dev.andresm.unieventosMongodb.documentos.Rol;

import java.time.LocalDateTime;

/**
 * DTO de proyección utilizado como resultado de consultas
 * mediante Aggregation en MongoDB.

 * Este DTO NO representa el documento completo Orden.
 * Contiene únicamente los campos proyectados en la etapa $project.

 * Se utiliza para:
 * - Listar órdenes de un usuario
 * - Listar órdenes por evento

 * Es construido directamente por MongoDB mediante @Aggregation,
 * evitando transformaciones manuales en el servicio.

 * @param idOrden Identificador único de la orden.
 * @param fecha Fecha de creación de la orden.
 * @param total Valor total pagado.
 * @param estado Estado actual de la orden (CREADA, PAGADA, FALLIDA).
 * @param email Correo electrónico de la cuenta asociada.
 * @param rol Rol de la cuenta (ADMINISTRADOR o CLIENTE).
 * @param estadoCuenta Estado actual de la cuenta.
 */

public record ItemOrdenDTO(
        String idOrden,
        LocalDateTime fecha,
        double total,
        EstadoOrden estado,
        String email,
        Rol rol,
        EstadoCuenta estadoCuenta
) { }
