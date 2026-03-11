package dev.andresm.unieventosMongodb.dto.orden;

/**
 * DTO utilizado para solicitar la creación de una nueva orden.

 * Contiene únicamente la información mínima necesaria para generar
 * una orden desde el carrito del cliente.
 *
 * @param idCliente Identificador de la cuenta que realiza la compra.
 * @param codigoCupon Código de cupón opcional aplicado a la orden.
 */
public record CrearOrdenDTO(
        String idCliente,
        String codigoCupon
) {}
