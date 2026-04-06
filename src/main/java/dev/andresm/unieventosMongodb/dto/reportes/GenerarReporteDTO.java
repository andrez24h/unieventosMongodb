package dev.andresm.unieventosMongodb.dto.reportes;

/**
 * DTO utilizado para solicitar la generación de un reporte.

 * Contiene únicamente el identificador del evento,
 * permitiendo que el servicio se encargue de obtener
 * toda la información necesaria.

 * Ventajas:
 * - Reduce acoplamiento
 * - Mejora la arquitectura
 * - Centraliza la lógica en el servicio
 *
 * @param idEvento identificador del evento a analizar
 */
public record GenerarReporteDTO(

        /**
         * Identificador del evento sobre el cual se generará el reporte.
         */
        String idEvento
) {}
