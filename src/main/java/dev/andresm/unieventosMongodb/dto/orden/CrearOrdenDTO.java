package dev.andresm.unieventosMongodb.dto.orden;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO utilizado para solicitar la creación de una nueva orden.

 * Contiene únicamente la información mínima necesaria para generar
 * una orden desde el carrito del cliente.
 *
 * @param idCliente Identificador de la cuenta que realiza la compra.
 * @param codigoCupon Código de cupón opcional aplicado a la orden.
 */
public record CrearOrdenDTO(

        @NotBlank String idCliente,
        @NotBlank String codigoCupon
) {}
