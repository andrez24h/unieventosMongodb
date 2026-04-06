package dev.andresm.unieventosMongodb.servicios.interfaces;

import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;

import java.util.Map;

/**
 * Servicio encargado de la gestión de pagos en la plataforma.

 * Responsabilidades:
 * - Generar la preferencia de pago en MercadoPago
 * - Consultar pagos en la pasarela
 * - Procesar notificaciones (webhook)

 * IMPORTANTE:
 * Este servicio NO crea órdenes.
 * Solo trabaja sobre órdenes previamente creadas.
 */
public interface PagoServicio {

    /**
     * Genera la preferencia de pago para una orden existente.
     *
     * @param idOrden identificador de la orden
     * @return preferencia generada en MercadoPago
     * @throws Exception si la orden no existe
     */
    Preference crearPreferenciaPago(String idOrden) throws Exception;

    /**
     * Consulta un pago en MercadoPago.
     *
     * @param idPago identificador del pago en la pasarela
     * @return objeto Payment
     * @throws Exception si ocurre error en la consulta
     */
    Payment obtenerPago(Long idPago) throws Exception;

    /**
     * Procesa una notificación enviada por MercadoPago (Webhook).

     * @param request mapa con los datos enviados por MercadoPago
     */
   void procesarWebhook(Map<String, Object> request);
}
