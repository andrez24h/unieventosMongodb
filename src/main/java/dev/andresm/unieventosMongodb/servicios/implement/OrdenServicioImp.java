package dev.andresm.unieventosMongodb.servicios.implement;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import dev.andresm.unieventosMongodb.documentos.*;
import dev.andresm.unieventosMongodb.dto.cupon.RedimirCuponDTO;
import dev.andresm.unieventosMongodb.dto.orden.CrearOrdenDTO;
import dev.andresm.unieventosMongodb.dto.orden.ItemOrdenDTO;
import dev.andresm.unieventosMongodb.repositorios.CuentaRepo;
import dev.andresm.unieventosMongodb.repositorios.CuponRepo;
import dev.andresm.unieventosMongodb.repositorios.EventoRepo;
import dev.andresm.unieventosMongodb.repositorios.OrdenRepo;
import dev.andresm.unieventosMongodb.servicios.interfaces.CuponServicio;
import dev.andresm.unieventosMongodb.servicios.interfaces.OrdenServicio;
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

    // =========================================================
    // MÉTODO: REALIZAR PAGO
    // =========================================================
    /**
     * Genera una preferencia de pago en MercadoPago
     * para una orden previamente creada en el sistema.

     * Flujo del proceso:
     * 1. Se obtiene la orden desde la base de datos.
     * 2. Se construyen los ítems que se enviarán a la pasarela.
     * 3. Se configuran credenciales y URLs de retorno.
     * 4. Se crea la preferencia en MercadoPago.
     * 5. Se guarda el código de la pasarela en la orden.

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

        // 4.Configurar credenciales de MercadoPago
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

     * Flujo:
     * 1. Verificar tipo de notificación.
     * 2. Obtener el id del pago enviado por MercadoPago.
     * 3. Consultar el pago en la pasarela.
     * 4. Obtener el id de la orden desde los metadatos.
     * 5. Actualizar la orden con la información del pago.
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

                // 5. Se crea el cliente de MercadoPago y se obtiene el pago con el id
                PaymentClient client = new PaymentClient();
                Payment payment = client.get(Long.parseLong(idPago));

                // 6. Obtener el id de la orden asociada al pago que viene en los metadatos
                String idOrden = payment.getMetadata().get("id_orden").toString();

                // 7. Se obtiene la orden guardada en la base de datos
                Orden orden = obtenerOrden(idOrden);

                // 8. Se crea el objeto Pago a partir de la respuesta de MercadoPago
                Pago pago = crearPago(payment);

                // 9. Se asigna el pago a la orden
                orden.setPago(pago);

                // NUEVO: actualizar estado e inventario
                if ("approved".equals(payment.getStatus())) {
                    orden.setEstado(EstadoOrden.PAGADA);

                    for (DetalleOrden detalle : orden.getItems()) {
                        // 9.1 Buscar evento
                        Optional<Evento> optionalEvento = eventoRepo.buscarId(detalle.getIdEvento());

                        if (optionalEvento.isEmpty()) {
                            throw new RuntimeException("Evento no encontrado");
                        }

                        Evento evento = optionalEvento.get();

                        // 9.2 Buscar localidad
                        Optional<Localidad> optionalLocalidad = evento.getLocalidades()
                                .stream()
                                        .filter(l ->l.getNombre().equals(detalle.getNombreLocalidad()))
                                                .findFirst();

                        if (optionalLocalidad.isEmpty()) {
                            throw new RuntimeException("Localidad no encontrada");
                        }

                        Localidad localidad = optionalLocalidad.get();

                        // 9.3 Aumentar entradas vendidas
                        localidad.setEntradasVendidas(
                                localidad.getEntradasVendidas() + detalle.getCantidad()
                        );

                        // 9.4 Recalcular porcentaje
                        double porcentaje =
                                (double) localidad.getEntradasVendidas() / localidad.getCapacidadMaxima() * 100;
                        localidad.setPorcentajeVenta(porcentaje);

                        eventoRepo.save(evento);
                    }
                } else if ("rejected".equals(payment.getStatus())) {
                    orden.setEstado(EstadoOrden.FALLIDA);
                }

                // 10. Guardar la orden actualizada
                ordenRepo.save(orden);
            }
        } catch (Exception e) {
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
        if (cuenta.getEstado().equals(EstadoCuenta.INACTIVO)) {
            throw new Exception("El cliente no se encuentra disponible");
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

        // 7. Guardar la orden en la base de datos
        ordenRepo.save(orden);

        // 8. Retornar el ID de la orden
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

        List<ItemOrdenDTO> lista = ordenRepo.listarOrdenesEvento(idEvento);

        if (lista.isEmpty()) {
            throw new Exception("No existen órdenes asociadas a este evento");
        }
        return lista;
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

        List<ItemOrdenDTO> lista = ordenRepo.listarOrdenesPorUsuario(idUsuario);

        if (lista.isEmpty()) {
            throw new Exception("El usuario no tiene órdenes registradas");
        }
        return lista;
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
 * // 1️⃣ Buscar evento
 * Optional<Evento> optionalEvento = eventoRepo.buscarId(detalle.getIdEvento());
 *
 * if (optionalEvento.isEmpty()) {
 *     throw new RuntimeException("Evento no encontrado");
 * }
 *
 * Evento evento = optionalEvento.get();
 *
 * // 2️⃣ Buscar localidad
 * Optional<Localidad> optionalLocalidad = evento.getLocalidades()
 *         .stream()
 *         .filter(l -> l.getNombre().equals(detalle.getNombreLocalidad()))
 *         .findFirst();
 *
 * if (optionalLocalidad.isEmpty()) {
 *     throw new RuntimeException("Localidad no encontrada");
 * }
 *
 * Localidad localidad = optionalLocalidad.get();
 */


