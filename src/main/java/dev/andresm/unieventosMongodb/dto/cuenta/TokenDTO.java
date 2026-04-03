package dev.andresm.unieventosMongodb.dto.cuenta;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO utilizado para enviar el token JWT al cliente
 * después de que el usuario se autentica correctamente.

 * Este objeto se utiliza normalmente como respuesta
 * del endpoint de inicio de sesión.

 * Contiene el token que el cliente deberá enviar
 * en el encabezado Authorization en las siguientes peticiones.
 */
public record TokenDTO (

        /**
         * Token JWT generado por el sistema.
         * No puede ser nulo ni vacío.
         */
        @NotBlank String token
) {}
