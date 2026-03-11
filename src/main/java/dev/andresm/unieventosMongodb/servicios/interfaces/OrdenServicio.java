package dev.andresm.unieventosMongodb.servicios.interfaces;

import com.mercadopago.resources.preference.Preference;
import dev.andresm.unieventosMongodb.documentos.Orden;
import dev.andresm.unieventosMongodb.dto.orden.CrearOrdenDTO;
import dev.andresm.unieventosMongodb.dto.orden.ItemOrdenDTO;
import dev.andresm.unieventosMongodb.dto.orden.OrdenDetalleDTO;

import java.util.List;
import java.util.Map;

/**
 * - Servicio de gestión de órdenes en UniEventos.
 * Define las operaciones relacionadas con la creación, consulta
 * y pago de órdenes de compra de entradas para eventos.

 * Incluye métodos para:
 * - Crear una orden
 * - Obtener una orden específica
 * - Realizar pagos mediante MercadoPago
 * - Recibir notificaciones de la pasarela de pago
 * - Listar órdenes por evento o por usuario
 */
public interface OrdenServicio {

    /**
     * - Realizar el pago de una orden específica.

     * @param idOrden identificador de la orden a pagar
     * @return Objeto Preference de MercadoPago con los datos de la transacción
     * @throws Exception si ocurre un error en la integración con la pasarela de pago
     */
    Preference realizarPago(String idOrden) throws Exception;

    /**
     * - Procesar notificaciones recibidas desde MercadoPago.
     * Permite actualizar el estado de la orden según los eventos enviados
     * por la pasarela de pagos (ej. pago aprobado, rechazado, pendiente).

     * @param request mapa con los datos de la notificación enviada por MercadoPago
     */
    void recibirNotificacionMercadoPago(Map<String, Object> request);

    /**
     * - Crear una nueva orden de compra.

     * @param crearOrdenDTO datos necesarios para crear la orden
     * @return Identificador generado para la nueva orden
     * @throws Exception si el cliente no existe, el cupón es inválido,
     *                   u ocurre cualquier error durante la creación
     */
    String crearOrden(CrearOrdenDTO crearOrdenDTO) throws Exception;

    /**
     * - Obtener los detalles completos de una orden.

     * @param idOrden identificador de la orden
     * @return Objeto Orden con toda la información de la compra
     * @throws Exception si la orden no existe
     */
    Orden obtenerOrden(String idOrden) throws Exception;

    /**
     * - Listar todas las órdenes que incluyen un evento específico.
     * Evita el uso de métodos derivados como findBy y utiliza lógica
     * de agregación para devolver información resumida.

     * @param idEvento identificador del evento
     * @return Lista de órdenes resumidas (ItemOrdenDTO) asociadas al evento
     */
    List<ItemOrdenDTO> listarOrdenesPorEvento(String idEvento) throws Exception;

    /**
     * - Listar todas las órdenes de un usuario específico.
     * Evita el uso de métodos derivados como findBy y utiliza lógica
     * de agregación para devolver información resumida.

     * @param idUsuario identificador del cliente/usuario
     * @return Lista de órdenes resumidas (ItemOrdenDTO) del usuario
     */
    List<ItemOrdenDTO> listarOrdenesPorUsuario(String idUsuario) throws Exception;

    /**
     * Obtiene la información detallada de una orden específica.
     * Incluye los ítems (DetalleOrden) asociados a la compra.

     * @param idOrden identificador único de la orden
     * @return DTO con la información completa de la orden y sus ítems
     * @throws Exception si la orden no existe en el sistema
     */
    OrdenDetalleDTO obtenerItemsOrden(String idOrden) throws Exception;

    /**
     * Valida si un cliente puede aplicar el cupón de primera compra.
     * Un cliente solo puede usar este cupón si no tiene órdenes registradas.

     * @param idCliente identificador del cliente
     * @return true si puede usar el cupón
     */
    boolean esPrimeraCompra(String idCliente);
}

/**
 *

 * Quita @Autowired por campo -> usa constructor injection
 * Saca el AccessToken a configuración
 * Maneja excepciones con una excepción personalizada
 * Agrega logs en vez de printStackTrace

 * Maneja transiciones de estado expl&iacute;citas

 * Si haces eso..;

 * Tu proyecto deja de ser académico y se vuelve portfolio serio.
 */
