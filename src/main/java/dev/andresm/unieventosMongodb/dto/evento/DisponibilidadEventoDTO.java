package dev.andresm.unieventosMongodb.dto.evento;

/**
 * DTO utilizado para verificar la disponibilidad de una localidad
 * dentro de un evento específico.
 *
 * @param idEvento         identificador del evento
 * @param nombreLocalidad  nombre de la localidad dentro del evento
 * @param cantidad         cantidad de entradas solicitadas
 */
public record DisponibilidadEventoDTO(
        String idEvento,
        String nombreLocalidad,
        int cantidad
) {
}
