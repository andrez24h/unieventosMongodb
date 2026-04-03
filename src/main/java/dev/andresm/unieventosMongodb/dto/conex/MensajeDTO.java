package dev.andresm.unieventosMongodb.dto.conex;

/**
 * DTO genérico utilizado para enviar respuestas estandarizadas
 * desde la API hacia el cliente.

 * Permite retornar:
 * - Si ocurrió un error
 * - Un mensaje descriptivo
 * - Una respuesta de cualquier tipo
 *
 * @param <T> tipo de dato que contendrá la respuesta
 */
public record MensajeDTO<T> (

        /**
         * Indica si ocurrió un error en la operación.
         */
        boolean error,

        /**
         * Mensaje descriptivo del resultado de la operación.
         */
        String mensaje,

        /**
         * Información o datos que se desean retornar al cliente.
         */
        T respuesta
) {}
