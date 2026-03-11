package dev.andresm.unieventosMongodb.documentos;

/**
 * Representa los posibles estados de una orden dentro del sistema.

 * - CREADA: La orden fue generada en el sistema pero aún no se ha iniciado el pago.
 * - PENDIENTE: La orden fue creada pero aún no se ha confirmado el pago.
 * - PAGADA: El pago fue aprobado correctamente por la pasarela.
 * - FALLIDA: El pago fue rechazado o ocurrió un error en la transacción.
 * - CANCELADA: La orden fue anulada manualmente o el pago fue rechazado.

 * Este enum permite controlar el flujo del proceso de compra
 * y el estado de la transacción asociada.
 */
public enum EstadoOrden {
    CREADA,     // Orden recién generada
    PENDIENTE,  // Esperando confirmación de pago
    PAGADA,     // Pago aprobado
    FALLIDA,    // Pago rechazado
    CANCELADA   // Orden anulada manualmente
}
