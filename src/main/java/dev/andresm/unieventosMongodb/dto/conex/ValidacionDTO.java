package dev.andresm.unieventosMongodb.dto.conex;

/**
 * DTO utilizado para representar errores de validación
 * en los datos enviados por el cliente.

 * Este objeto se utiliza normalmente cuando ocurre una
 * validación fallida en los datos de entrada de un endpoint
 * (por ejemplo, cuando un campo obligatorio está vacío).

 * Permite identificar:
 * - El campo que produjo el error
 * - El mensaje descriptivo de la validación
 */
public record ValidacionDTO(

        /**
         * Nombre del campo que generó el error de validación.
         */
        String campo,

        /**
         * Mensaje que describe el error de validación.
         */
        String mensaje
){
}
