package dev.andresm.unieventosMongodb.dto.reportes;

import dev.andresm.unieventosMongodb.documentos.Evento;
import dev.andresm.unieventosMongodb.documentos.Orden;

import java.util.List;

/**
 * DTO utilizado para la generación de reportes de un evento.

 * Contiene:
 * - El evento del cual se generará el reporte.
 * - La lista de órdenes asociadas a dicho evento.

 * Este DTO permite encapsular toda la información necesaria
 * para construir reportes como:
 * - Reportes de ventas
 * - Reportes de asistencia
 * - Reportes financieros

 * Se implementa como un record para garantizar:
 * - Inmutabilidad
 * - Código más limpio
 * - Reducción de boilerplate (getters, constructor, equals, hashCode)
 */
public record GenerarReporteDTO(

        /**
         * Evento sobre el cual se generará el reporte.
         */
        Evento evento,

        /**
         * Lista de órdenes asociadas al evento.
         * Puede representar ventas, reservas o registros de asistencia.
         */
        List<Orden> ordenes
) {
}
