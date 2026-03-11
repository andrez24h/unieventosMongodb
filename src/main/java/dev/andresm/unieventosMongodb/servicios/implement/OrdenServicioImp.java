package dev.andresm.unieventosMongodb.servicios.implement;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import com.thoughtworks.qdox.model.expression.Or;
import dev.andresm.unieventosMongodb.documentos.*;
import dev.andresm.unieventosMongodb.dto.cupon.CrearCuponDTO;
import dev.andresm.unieventosMongodb.dto.cupon.RedimirCuponDTO;
import dev.andresm.unieventosMongodb.dto.email.EmailDTO;
import dev.andresm.unieventosMongodb.dto.orden.CrearOrdenDTO;
import dev.andresm.unieventosMongodb.dto.orden.ItemOrdenDTO;
import dev.andresm.unieventosMongodb.dto.orden.ItemOrdenDetalleDTO;
import dev.andresm.unieventosMongodb.dto.orden.OrdenDetalleDTO;
import dev.andresm.unieventosMongodb.repositorios.CuentaRepo;
import dev.andresm.unieventosMongodb.repositorios.CuponRepo;
import dev.andresm.unieventosMongodb.repositorios.EventoRepo;
import dev.andresm.unieventosMongodb.repositorios.OrdenRepo;
import dev.andresm.unieventosMongodb.servicios.interfaces.CuponServicio;
import dev.andresm.unieventosMongodb.servicios.interfaces.EmailServicio;
import dev.andresm.unieventosMongodb.servicios.interfaces.OrdenServicio;
import dev.andresm.unieventosMongodb.servicios.interfaces.QRServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación del servicio de órdenes.

 * Responsabilidades:
 * - Crear órdenes a partir del carrito
 * - Generar preferencia de pago en MercadoPago
 * - Procesar notificaciones (webhook)
 * - Consultar órdenes por evento o usuario

 * NOTA:
 * El precio se congela en el DetalleOrden al momento de crear la orden.
 * No se recalcula al generar el pago.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class OrdenServicioImp implements OrdenServicio {

    private final CuentaRepo cuentaRepo;
    private final CuponRepo cuponRepo;
    private final CuponServicio cuponServicio;
    private final EventoRepo eventoRepo;
    private final OrdenRepo ordenRepo;
    private final EmailServicio emailServicio;
    private final QRServicio qrServicio;

    // =========================================================
    // MÉTODO: REALIZAR PAGO
    // =========================================================

    /**
     * Genera una preferencia de pago en MercadoPago
     * para una orden previamente creada en el sistema.
     * <p>
     * Flujo del proceso:
     * 1. Se obtiene la orden desde la base de datos.
     * 2. Se construyen los ítems que se enviarán a la pasarela.
     * 3. Se configuran credenciales y URLs de retorno.
     * 4. Se crea la preferencia en MercadoPago.
     * 5. Se guarda el código de la pasarela en la orden.
     * <p>
     * IMPORTANTE:
     * El precio ya fue validado y almacenado en el DetalleOrden
     * al momento de crear la orden (precio congelado).
     * No se recalcula el precio al generar el pago.
     */
    @Override
    public Preference realizarPago(String idOrden) throws Exception {

        // 1.  Obtener la orden guardada en la base de datos y los ítems de la orden
        Orden ordenGuardada = obtenerOrden(idOrden);

        // 2. Lista que contendrá los ítems que se enviarán a MercadoPago
        List<PreferenceItemRequest> itemsPasarela = new ArrayList<>();

        // 3. Recorrer los items de la orden y crea los ítems de la pasarela
        for (DetalleOrden item : ordenGuardada.getItems()) {

            /*
             * IMPORTANTE:
             * No volvemos a consultar el evento ni la localidad.
             * El precio ya fue validado y guardado cuando se creó la orden.
             * Esto evita inconsistencias si el precio cambia después.
             */
            PreferenceItemRequest itemRequest =
                    PreferenceItemRequest.builder()
                            .id(item.getIdEvento())             // Identificador del evento
                            .title(item.getNombreLocalidad())   // Nombre de la localidad
                            .quantity(item.getCantidad())       // Cantidad de entradas
                            .currencyId("COP")                  // Moneda
                            .unitPrice(BigDecimal.valueOf(item.getPrecioUnitario()))
                            .build();

            itemsPasarela.add(itemRequest);
        }

        // 4. Configurar credenciales de MercadoPago
        MercadoPagoConfig.setAccessToken("ACCESS_TOKEN");

        // 5. Configurar URLs de retorno (Frontend)
        PreferenceBackUrlsRequest backUrls =
                PreferenceBackUrlsRequest.builder()
                        .success("URL PAGO EXITOSO")
                        .failure("URL_PAGO_FALLIDO")
                        .pending("URL_PAGO_PENDIENTE")
                        .build();

        /*
         * 6️. Construir la preferencia:
         * - Ítems
         * - Metadatos (para recuperar la orden en el webhook)
         * - URLs de retorno
         * - URL de notificación (Webhook con ngrok)
         */

        PreferenceRequest preferenceRequest =
                PreferenceRequest.builder()
                        .items(itemsPasarela)
                        .backUrls(backUrls)
                        .metadata(Map.of("id_orden", ordenGuardada.getId()))
                        .notificationUrl("URL NOTIFICACION")
                        .build();

        // 7️. Crear la preferencia en MercadoPago
        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        // 8. Guardar el código de la pasarela en la orden
        ordenGuardada.setCodigoPasarela(preference.getId());
        ordenRepo.save(ordenGuardada);

        // 9. Retornar la preferencia generada
        return preference;
    }

    // =========================================================
    // MÉTODO: RECIBIR NOTIFICACIONES (WEBHOOK)
    // =========================================================

    /**
     * Procesa las notificaciones enviadas por MercadoPago
     * a través del Webhook configurado en notificationUrl.

     * Solo se procesan notificaciones de tipo "payment".

     * Flujo del proceso:
     * 1. Verificar el tipo de notificación recibida.
     * 2. Obtener el identificador del pago enviado por MercadoPago.
     * 3. Consultar el pago en la pasarela utilizando el id recibido.
     * 4. Recuperar el id de la orden almacenado en los metadatos del pago.
     * 5. Buscar la orden correspondiente en la base de datos.
     * 6. Construir el objeto Pago del sistema a partir de la respuesta de la pasarela.
     * 7. Asociar el pago a la orden.
     * 8. Si el pago fue aprobado:
     *      - Actualizar el estado de la orden a PAGADA.
     *      - Actualizar el inventario de entradas del evento.
     *      - Enviar el correo de confirmación de compra al cliente.
     * 9. Si el pago fue rechazado:
     *      - Marcar la orden como FALLIDA.
     * 10. Guardar la orden actualizada en la base de datos.
     */
    @Override
    public void recibirNotificacionMercadoPago(Map<String, Object> request) {

        try {

            // 1. Obtener el tipo de notificación
            Object tipo = request.get("type");

            // 2. Si la notificación es de un pago entonces obtener el pago y la orden asociada
            if ("payment".equals(tipo)) {

                // 3. Capturamos el JSON que viene en el request y lo convertimos a un String
                String input = request.get("data").toString();

                // 4. Extraemos los números de la cadena, es decir, el id del pago
                String idPago = input.replaceAll("\\D+", "");

                // 5️. Consultar el pago en MercadoPago utilizando su API
                PaymentClient client = new PaymentClient();
                Payment payment = client.get(Long.parseLong(idPago));

                // 6️. Obtener el id de la orden almacenado en los metadatos del pago
                String idOeden = payment.getMetadata().get("id_orden").toString();

                // 7️. Buscar la orden en la base de datos
                Orden orden = obtenerOrden(idOeden);

                // 8️. Crear el objeto Pago del sistema a partir de la respuesta de MercadoPago
                Pago pago = crearPago(payment);

                // 9️⃣ Asociar el pago a la orden
                orden.setPago(pago);

                // =========================================================
                // PROCESAR RESULTADO DEL PAGO
                // =========================================================

                // 10️. Si el pago fue aprobado
                if ("approved".equals(payment.getStatus())) {

                    // Marcar la orden como pagada
                    orden.setEstado(EstadoOrden.PAGADA);

                    // - Enviar correo de confirmación de compra
                    // Se construye el objeto EmailDTO con la información de la orden
                    EmailDTO email = new EmailDTO(
                            "cliente@email.com",
                            "Factura de compra - UniEventos",
                            "Su compra fue realizada con éxito.\n\nOrden: " + orden.getId()
                    );

                    // Se utiliza el servicio de correo del sistema
                    emailServicio.enviarEmail(email);

                    // =====================================================
                    // ACTUALIZAR INVENTARIO DE ENTRADAS
                    // =====================================================

                    for (DetalleOrden detalle : orden.getItems()) {

                        // 10.1 Buscar el evento asociado al ítem
                        Optional<Evento> optionalEvento = eventoRepo.buscarId(detalle.getIdEvento());

                        if (optionalEvento.isEmpty()) {
                            throw new RuntimeException("Evento no encontrado");
                        }

                        Evento evento = optionalEvento.get();

                        // 10.2 Buscar la localidad dentro del evento
                        Optional<Localidad> optionalLocalidad = evento.getLocalidades()
                                .stream()
                                .filter(localidad -> localidad.getNombre().equals(detalle.getNombreLocalidad()))
                                .findFirst();

                        if (optionalLocalidad.isEmpty()) {
                            throw new RuntimeException("Localidad no encontrada");
                        }

                        Localidad localidad = optionalLocalidad.get();

                        // 10.3 Aumentar el número de entradas vendidas
                        localidad.setEntradasVendidas(
                                localidad.getEntradasVendidas() + detalle.getCantidad()
                        );

                        // 10.4 Recalcular el porcentaje de venta de la localidad
                        double porcentaje =
                                (double) localidad.getEntradasVendidas() / localidad.getCapacidadMaxima() * 100;
                        localidad.setPorcentajeVenta(porcentaje);

                        // 10.5 Guardar el evento actualizado
                        eventoRepo.save(evento);
                    }

                    // 11️. Si el pago fue rechazado
                } else if ("rejected".equals(payment.getStatus())) {

                    // Marcar la orden como fallida
                    orden.setEstado(EstadoOrden.FALLIDA);
                }

                // 12️. Guardar la orden actualizada en la base de datos
                ordenRepo.save(orden);
            }
        } catch (Exception e) {

            // Manejo básico de errores para evitar que el webhook falle
            e.printStackTrace();
        }
    }

    /* =====================   MÉTODOS AUXILIAR   ========================================= */

    /**
     * Convierte un objeto Payment de MercadoPago
     * en un objeto Pago utilizado por el sistema.
     */
    private Pago crearPago(Payment payment) {

        // Convertir el objeto Payment de MercadoPago
        // en un objeto Pago del sistema
        Pago pago = new Pago();

        pago.setCodigo(payment.getId().toString());                             // Id del pago en MercadoPago
        pago.setCodigoAutorizacion(payment.getAuthorizationCode());             // Código de autorización bancaria
        pago.setDetalleEstado(payment.getStatusDetail());                       // Detalle técnico del estado
        pago.setEstado(payment.getStatus());                                    // Estado general (approved, rejected, pending)
        pago.setFecha(payment.getDateCreated().toLocalDateTime());              // Fecha de creación del pago
        pago.setMoneda(payment.getCurrencyId());                                // Moneda
        pago.setTipoPago(payment.getPaymentTypeId());                           // Tipo de pago (credit_card, debit_card, etc.)
        pago.setValorTransaccion(payment.getTransactionAmount().floatValue());  // Valor pagado

        return pago;
    }

    @Override
    public String crearOrden(CrearOrdenDTO crearOrdenDTO) throws Exception {

        // 1️. Buscar la cuenta del cliente
        Optional<Cuenta> optionalCuenta = cuentaRepo.buscarId(crearOrdenDTO.idCliente());

        if (optionalCuenta.isEmpty()) {
            throw new Exception("El cliente no existe");
        }
        Cuenta cuenta = optionalCuenta.get();

        // 2. Validar que la cuenta esté activa
        if (cuenta.getEstado() == EstadoCuenta.INACTIVO) {  // equals(EstadoCuenta.INACTIVO)), Los enum en Java son instancias únicas, entonces == es
            throw new Exception("El cliente no se encuentra disponible");                  // (más rápido, más limpio, estándar para enums)
        }

        // 3 Buscar el cupón si se proporcionó
        Optional<Cupon> optionalCupon = cuponRepo.buscarCodigo(crearOrdenDTO.codigoCupon());
        Cupon cupon = null;
        boolean cuponRedimido = false;

        if (optionalCupon.isPresent()) {
            cupon = optionalCupon.get();

            // Redimir cupón usando el servicio
            RedimirCuponDTO redimirCuponDTO = new RedimirCuponDTO(
                    crearOrdenDTO.codigoCupon(),
                    crearOrdenDTO.idCliente()
            );
            cuponRedimido = cuponServicio.redimirCupon(redimirCuponDTO);
        }

        // 4. Crear la orden
        Orden orden = new Orden();
        orden.setIdCliente(crearOrdenDTO.idCliente());
        orden.setFecha(LocalDateTime.now());

        if (cuponRedimido) {
            orden.setIdCupon(cupon.getId());
        }

        List<DetalleOrden> detallesOrden = new ArrayList<>();
        float total = 0;

        // 5. Procesar los ítems del carrito
        for (DetalleCarrito itemCarrito : cuenta.getCarrito().getItems()) {
            // Buscar el evento usando Optional como en tu código
            Optional<Evento> optionalEvento = eventoRepo.buscarId(itemCarrito.getIdEvento());
            if (optionalEvento.isEmpty()) {
                throw new Exception("Evento no encontrado");
            }
            Evento evento = optionalEvento.get();

            // Buscar localidad del evento
            Optional<Localidad> optionalLocalidad = evento.getLocalidades().stream()
                    .filter(localidad -> localidad.getNombre().equals(itemCarrito.getNombreLocalidad()))
                    .findFirst();
            if (optionalLocalidad.isEmpty()) {
                throw new Exception("Localidad no encontrada");
            }
            Localidad localidad = optionalLocalidad.get();

            // Validar aforo disponible
            int aforoDisponible = localidad.getCapacidadMaxima() - localidad.getEntradasVendidas();
            if (aforoDisponible < itemCarrito.getCantidad()) {
                throw new Exception("No hay suficientes entradas disponibles para " + localidad.getNombre());
            }
            //  No descontamos entradas todavía, se hará al confirmar pago

            // Crear detalle de orden
            DetalleOrden detalleOrden = new DetalleOrden();
            detalleOrden.setIdEvento(itemCarrito.getIdEvento());
            detalleOrden.setNombreLocalidad(itemCarrito.getNombreLocalidad());
            detalleOrden.setPrecioUnitario(localidad.getPrecio());
            detalleOrden.setCantidad(itemCarrito.getCantidad());
            detallesOrden.add(detalleOrden);

            // Acumular total
            total += (float) (localidad.getPrecio() * itemCarrito.getCantidad());
        }

        // 6. Aplicar descuento si se redimió cupón
        if (cuponRedimido) {
            double descuento = cupon.getDescuento();
            total -= (float) (total * (descuento / 100));
        }

        orden.setItems(detallesOrden);
        orden.setTotal(total);
        //  NUEVO: Estado inicial de la orden
        orden.setEstado(EstadoOrden.CREADA);

        /** =========================================================
        //      CUPÓN POR PRIMERA COMPRA
        // =========================================================

         * Si el cliente está realizando su primera compra en el sistema
         * se genera automáticamente un cupón individual de descuento
         * del 10% para futuras compras.

         * El cupón es creado utilizando el servicio de cupones y
         * posteriormente se envía al correo del cliente registrado
         * en la plataforma.
         */
        // 6.1 Verificar si es la primera compra
        boolean primeraCompra = esPrimeraCompra(crearOrdenDTO.idCliente());

        if (primeraCompra) {

            List<String> clientes = List.of(crearOrdenDTO.idCliente());

            String codigoCupon = cuponServicio.crearCupon(
                    new CrearCuponDTO(
                            "Cupón Primera Compra",
                            "Descuento del 10% por tu primera compra",
                            10,
                            LocalDateTime.now().plusYears(1),
                            TipoCupon.INDIVIDUAL,
                            clientes
                    )
            );

            EmailDTO emailCupon = new EmailDTO(
                    cuenta.getEmail(),
                    "Cupón por tu primera compra - UniEventos",
                    "Gracias por tu primera compra.\n\nTu código es:\n" + codigoCupon
            );

            emailServicio.enviarEmail(emailCupon);
        }

        // 7. Guardar la orden en la base de datos
        ordenRepo.save(orden);

        // 8. Generar contenido del QR para la orden
        String contenidoQR = "ORDEN:" + orden.getId();

        // 9. Generar el QR en Base64
        String qrBase64 = qrServicio.generarQR(contenidoQR);

        // 10. Construir el contenido del correo (sin HTML)
        String mensajeEmail =
                "Compra confirmada\n\n" +
                        "Orden: " + orden.getId() + "\n" +
                        "Fecha: " + orden.getFecha() + "\n" +
                        "Total: " + orden.getTotal() + "\n\n" +
                        "Este es el código QR de su entrada:\n" +
                        qrBase64;

        // 11. Crear el DTO del correo
        EmailDTO emailDTO = new EmailDTO(
                cuenta.getEmail(),
                "Confirmación de compra - UniEventos",
                mensajeEmail
        );

        // 12. Enviar el correo
        emailServicio.enviarEmail(emailDTO);

        // 13. Vaciar carrito
        cuenta.getCarrito().getItems().clear();
        cuentaRepo.save(cuenta);

        // 14. Retornar ID
        return orden.getId();
    }

    /**
     * Obtiene una orden por su ID.
     *
     * @param idOrden Identificador único de la orden
     * @return Orden encontrada
     * @throws Exception Si la orden no existe
     */
    @Override
    public Orden obtenerOrden(String idOrden) throws Exception {

        Optional<Orden> optionalOrden = ordenRepo.buscarId(idOrden);

        if (optionalOrden.isEmpty()) {
            throw new Exception("La orden no fue encontrada");
        }

        return optionalOrden.get();
    }

    /**
     * Lista todas las órdenes asociadas a un evento específico.

     * Flujo:
     * 1. Se consulta el repositorio utilizando una agregación.
     * 2. Se obtienen los datos resumidos en formato ItemOrdenDTO.
     * 3. Se valida que existan resultados.
     *
     * @param idEvento Identificador del evento
     * @return Lista de órdenes resumidas del evento
     * @throws Exception Si no existen órdenes asociadas al evento
     */
    @Override
    public List<ItemOrdenDTO> listarOrdenesPorEvento(String idEvento) throws Exception {

        List<ItemOrdenDTO> ordenes = ordenRepo.listarOrdenesPorEvento(idEvento);

        if (ordenes.isEmpty()) {
            throw new Exception("No existen órdenes asociadas a este evento");
        }
        return ordenes;
    }

    /**
     * Lista todas las órdenes realizadas por un usuario específico.

     * Flujo:
     * 1. Se consulta el repositorio utilizando una agregación.
     * 2. Se obtienen los datos resumidos en formato ItemOrdenDTO.
     * 3. Se valida que el usuario tenga órdenes registradas.
     *
     * @param idUsuario Identificador del usuario
     * @return Lista de órdenes resumidas del usuario
     * @throws Exception Si el usuario no tiene órdenes registradas
     */
    @Override
    public List<ItemOrdenDTO> listarOrdenesPorUsuario(String idUsuario) throws Exception {

        List<ItemOrdenDTO> ordenes = ordenRepo.listarOrdenesPorUsuario(idUsuario);

        if (ordenes.isEmpty()) {
            throw new Exception("El usuario no tiene órdenes registradas");
        }
        return ordenes;
    }

    /**
     * Obtiene el detalle completo de una orden incluyendo
     * los ítems comprados por el cliente.

     * Este método realiza:

     * 1. Búsqueda de la orden en la base de datos.
     * 2. Conversión de los objetos DetalleOrden a DTO.
     * 3. Generación de un código QR para cada ítem de la orden comprada.
     * 4. Construcción del DTO final OrdenDetalleDTO.

     * El QR se genera utilizando el servicio QRServicio y
     * contiene información que identifica la entrada para
     * su validación en el evento.

     * @param idOrden Identificador de la orden.
     * @return OrdenDetalleDTO con el detalle completo.
     * @throws Exception si la orden no existe o falla la generación del QR.
     */
    @Override
    public OrdenDetalleDTO obtenerItemsOrden(String idOrden) throws Exception {

        // 1. Obtener la orden almacenada en la base de datos
        Orden orden = obtenerOrden(idOrden);

        // 2. Convertir cada DetalleOrden en un DTO incluyendo su QR
        List<ItemOrdenDetalleDTO> itemsDTO = orden.getItems().stream()
                .map(item -> {

                    try {

                        /**
                         * 3. Construcción del contenido que se codificará
                         * dentro del código QR.

                         * Este contenido puede ser utilizado posteriormente
                         * para validar la entrada en el evento.
                         */
                        String contenidoQR =
                                "ORDEN:" + orden.getId() +
                                "|EVENTO:" + item.getIdEvento() +
                                "|LOCALIDAD:" + item.getNombreLocalidad() +
                                "|CANTIDAD:" + item.getCantidad();

                        /**
                         * 4. Generar el QR utilizando el servicio QR
                         */
                        String qrBase64 = qrServicio.generarQR(contenidoQR);

                        /**
                         * 5. Construir el DTO del ítem incluyendo el QR
                         */
                        return new ItemOrdenDetalleDTO(
                                item.getIdEvento(),
                                item.getCantidad(),
                                item.getPrecioUnitario(),
                                item.getNombreLocalidad(),
                                qrBase64
                        );
                    } catch (Exception e) {
                        throw new RuntimeException("Error generando QR");
                    }
                }) .toList();
        /**
         * 6. Construcción del DTO final que contiene
         * la información general de la orden
         */
        return new OrdenDetalleDTO(
                orden.getId(),
                orden.getIdCliente(),
                orden.getFecha(),
                orden.getTotal(),
                orden.getEstado().name(),
                itemsDTO
        );
    }

    /**
     * Verifica si el cliente está realizando su primera compra.

     * Flujo:
     * 1. Consultar las órdenes existentes del cliente.
     * 2. Si no existen órdenes, se considera primera compra.

     * @param idCliente identificador del cliente
     * @return true si el cliente no tiene órdenes registradas
     */
    @Override
    public boolean esPrimeraCompra(String idCliente) {

        // 1️. Consultar en la base de datos las órdenes del cliente
        List<Orden> ordenes = ordenRepo.buscarOrdenesPorCliente(idCliente);

        // 2️. Si la lista está vacía significa que el cliente aún no ha realizado compras
        //    por lo tanto se considera su primera compra
        return ordenes.isEmpty();
    }
}

/**
 Evento evento = eventoRepo.buscarId(detalle.getIdEvento())
 .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

 Localidad localidad = evento.getLocalidades().stream()
 .filter(l -> l.getNombre().equals(detalle.getNombreLocalidad()))
 .findFirst()
 .orElseThrow(() -> new RuntimeException("Localidad no encontrada"));
 */

/**
 * // 1️. Buscar evento
 * Optional<Evento> optionalEvento = eventoRepo.buscarId(detalle.getIdEvento());

 * if (optionalEvento.isEmpty()) {
 *     throw new RuntimeException("Evento no encontrado");
 * }

 * Evento evento = optionalEvento.get();

 * // 2️. Buscar localidad
 * Optional<Localidad> optionalLocalidad = evento.getLocalidades()
 *         .stream()
 *         .filter(l -> l.getNombre().equals(detalle.getNombreLocalidad()))
 *         .findFirst();

 * if (optionalLocalidad.isEmpty()) {
 *     throw new RuntimeException("Localidad no encontrada");
 * }

 * Localidad localidad = optionalLocalidad.get();
 */


