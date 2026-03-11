package dev.andresm.unieventosMongodb.dto.evento;

import dev.andresm.unieventosMongodb.documentos.TipoEvento;

import java.time.LocalDate;

/**
 * DTO utilizado para aplicar filtros dinámicos en la búsqueda de eventos.

 * Todos los atributos son opcionales.
 * Solo se aplicarán como criterio de búsqueda aquellos que no sean nulos
 * (o vacíos en el caso de String).

 * Este DTO permite realizar búsquedas combinadas sin necesidad
 * de definir múltiples métodos específicos en el repositorio.

 * Ejemplos de uso:
 * - Buscar por ciudad solamente.
 * - Buscar por tipo y fecha.
 * - Buscar por nombre parcial.
 * - Buscar combinando todos los criterios.

 * @param nombre Nombre parcial o completo del evento.
 *               La búsqueda debe realizarse de manera insensible
 *               a mayúsculas y minúsculas.

 * @param tipo Tipo de evento (CONCIERTO, TEATRO, DEPORTE, etc.).
 *
 * @param ciudad Ciudad donde se realiza el evento.
 *               Puede utilizarse coincidencia parcial.

 * @param fecha Fecha específica del evento.
 *              Se recomienda consultar dentro del rango completo del día
 *              (desde 00:00:00 hasta 23:59:59).
 */
public record FiltroEventoDTO(
        String nombre,
        TipoEvento tipo,
        String ciudad,
        LocalDate fecha
) {}
