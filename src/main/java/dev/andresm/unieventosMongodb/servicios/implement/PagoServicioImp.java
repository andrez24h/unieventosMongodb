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
import dev.andresm.unieventosMongodb.dto.email.EmailDTO;
import dev.andresm.unieventosMongodb.repositorios.CuentaRepo;
import dev.andresm.unieventosMongodb.repositorios.EventoRepo;
import dev.andresm.unieventosMongodb.repositorios.OrdenRepo;
import dev.andresm.unieventosMongodb.servicios.interfaces.EmailServicio;
import dev.andresm.unieventosMongodb.servicios.interfaces.PagoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementación del servicio de pagos.

 * Responsabilidades:
 * - Generar preferencias de pago en MercadoPago
 * - Consultar pagos en la pasarela
 * - Procesar notificaciones (webhooks)
 * - Actualizar el estado de las órdenes según el resultado del pago
 * - Gestionar efectos secundarios (email, inventario)

 * IMPORTANTE:
 * Este servicio NO crea órdenes.
 * Solo trabaja sobre órdenes previamente registradas.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PagoServicioImp implements PagoServicio {

    private final OrdenRepo ordenRepo;
    private final CuentaRepo cuentaRepo;
    private final EventoRepo eventoRepo;
    private final EmailServicio emailServicio;

    //@Value("${mercadopago.access.token}")
    private String accessToken;

    @Value("${mercadopago.url.success}")
    private String urlSuccess;

    @Value("${mercadopago.url.failure}")
    private String urlFailure;

    @Value("${mercadopago.url.pending}")
    private String urlPending;

    @Value("${mercadopago.url.webhook}")
    private String urlWebhook;

    // =========================================================
    // MÉTODO: CREAR PREFERENCIA DE PAGO
    // =========================================================

    @Override
    public Preference crearPreferenciaPago(String idOrden) throws Exception {

        // 1. Buscar la orden en base de datos
        Optional<Orden> optionalOrden = ordenRepo.buscarId(idOrden);

        if (optionalOrden.isEmpty()) {
            throw new Exception("La orden no existe");
        }

        Orden orden = optionalOrden.get();

        // 1.5 Validar que la orden no esté ya pagada
        if (orden.getEstado() == EstadoOrden.PAGADA) {
            throw new Exception("La orden ya fue pagada");
        }

        // 2. Construir ítems para la pasarela
        List<PreferenceItemRequest> items = new ArrayList<>();

        for (DetalleOrden item : orden.getItems()) {

            PreferenceItemRequest itemRequest =
                    PreferenceItemRequest.builder()
                            .id(item.getIdEvento())
                            .title(item.getNombreLocalidad())
                            .quantity(item.getCantidad())
                            .currencyId("COP")
                            .unitPrice(BigDecimal.valueOf(item.getPrecioUnitario()))
                            .build();

            items.add(itemRequest);
        }

        // 3. Configurar credenciales (temporal - hardcoded para pruebas)
        MercadoPagoConfig.setAccessToken("TEST-123");  // (accessToken) con @Value

        // 4. Configurar URLs de redirección después del pago
        // success  -> pago aprobado
        // failure  -> pago rechazado
        // pending  -> pago pendiente
        PreferenceBackUrlsRequest backUrls =
                PreferenceBackUrlsRequest.builder()
                        .success(urlSuccess)
                        .failure(urlFailure)
                        .pending(urlPending)
                        .build();

        // 5. Crear preferencia
        PreferenceRequest request =
                PreferenceRequest.builder()
                        .items(items)
                        .metadata(Map.of("id_orden", orden.getId()))
                        .notificationUrl(urlWebhook)
                        .backUrls(backUrls)
                        .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(request);

        // 6. Guardar referencia en la orden
        orden.setCodigoPasarela(preference.getId());
        ordenRepo.save(orden);

        return preference;
    }

    // =========================================================
    // MÉTODO: CONSULTAR PAGO
    // =========================================================

    /**
     * Consulta un pago en MercadoPago usando su ID.
     */
    @Override
    public Payment obtenerPago(Long idPago) throws Exception {

        MercadoPagoConfig.setAccessToken("TEST-123");

        PaymentClient client = new PaymentClient();

        return client.get(idPago);
    }

    // =========================================================
    // MÉTODO: PROCESAR WEBHOOK
    // =========================================================

    /**
     * Procesa la notificación enviada por MercadoPago.

     * Flujo completo:
     * 1. Obtener tipo de notificación
     * 2. Validar que sea tipo "payment"
     * 3. Obtener objeto data
     * 4. Validar estructura de data
     * 5. Validar existencia del id del pago
     * 6. Convertir id a Long
     * 7. Consultar pago en MercadoPago
     * 8. Obtener metadata del pago
     * 9. Validar existencia de id_orden en metadata
     * 10. Obtener id de la orden
     * 11. Buscar orden en base de datos
     * 12. Validar que la orden exista
     * 13. Evitar reprocesamiento de órdenes pagadas
     * 14. Crear objeto Pago del sistema
     * 15. Asociar pago a la orden
     * 16. Evaluar estado del pago
     * 17. Si aprobado → actualizar estado de la orden
     * 18. Obtener cuenta del cliente
     * 19. Enviar email de confirmación
     * 20. Recorrer items de la orden
     * 21. Obtener evento
     * 22. Obtener localidad
     * 23. Actualizar entradas vendidas
     * 24. Recalcular porcentaje
     * 25. Guardar evento
     * 26. Si rechazado → marcar orden como FALLIDA
     * 27. Guardar orden
     * 28. Manejo de errores (no romper webhook)
     */
    @Override
    public void procesarWebhook(Map<String, Object> request) {

        try {

            // 1. Obtener tipo de notificación
            Object tipo = request.get("type");

            // 2. Validar que sea tipo payment
            if (!"payment".equals(tipo)) {
                return;
            }

            // 3. Obtener data
            Object dataObj = request.get("data");

            // 4. Validar estructura
            if (!(dataObj instanceof Map)) {
                throw new RuntimeException("Formato inválido");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) dataObj;

            // 5. Validar id del pago
            if (data.get("id") == null) {
                throw new RuntimeException("No viene id");
            }

            // 6. Convertir id
            Long idPago = Long.parseLong(data.get("id").toString());

            // 7. Consultar pago en MercadoPago
            Payment payment = obtenerPago(idPago);

            // 8. Obtener metadata
            Map<String, Object> metadata = payment.getMetadata();

            // 9. Validar metadata
            if (metadata == null || metadata.get("id_orden") == null) {
                System.out.println("Sin id_orden");
                return;
            }

            // 10. Obtener id orden
            String idOrden = metadata.get("id_orden").toString();

            // 11. Buscar orden
            Optional<Orden> optionalOrden = ordenRepo.buscarId(idOrden);

            // 12. Validar existencia
            if (optionalOrden.isEmpty()) {
                System.out.println("No existe la orden");
                return;
            }

            Orden orden = optionalOrden.get();

            // 13. Evitar reprocesamiento
            if (orden.getEstado() == EstadoOrden.PAGADA)
                return;

            // 14. Crear pago
            Pago pago = crearPago(payment);

            // 15. Asociar pago
            orden.setPago(pago);

            // =====================================================
            // RESULTADO DEL PAGO
            // =====================================================

            // 16. Evaluar estado
            if ("approved".equals(payment.getStatus())) {

                // 17. Marcar como pagada
                orden.setEstado(EstadoOrden.PAGADA);

                // 18. Obtener cuenta
                Optional<Cuenta> optionalCuenta = cuentaRepo.buscarId(orden.getIdCliente());

                if (optionalCuenta.isPresent()) {

                    Cuenta cuenta = optionalCuenta.get();

                    // 19. Enviar email
                    EmailDTO email = new EmailDTO(
                            cuenta.getEmail(),
                            "Confirmación de compra",
                            "Orden confirmada: " + orden.getId()
                    );


                    emailServicio.enviarEmail(email);
                }

                // =================================================
                // INVENTARIO
                // =================================================

                // 20. Recorrer items
                for (DetalleOrden detalle : orden.getItems()) {

                    // 21. Obtener evento
                    Optional<Evento> optionalEvento = eventoRepo.buscarId(detalle.getIdEvento());

                    if (optionalEvento.isEmpty()) {
                        System.out.println("Evento no encontrado");
                        continue;
                    }

                    Evento evento = optionalEvento.get();

                    // 22. Obtener localidad
                    Optional<Localidad> optionalLocalidad = evento.getLocalidades()
                            .stream()
                            .filter(l -> l.getNombre().equals(detalle.getNombreLocalidad()))
                            .findFirst();

                    if (optionalLocalidad.isEmpty()) {
                        System.out.println("Localidad no encontrada");
                        continue;
                    }

                    Localidad localidad = optionalLocalidad.get();

                    // 23. Actualizar ventas
                    localidad.setEntradasVendidas(
                            localidad.getEntradasVendidas() + detalle.getCantidad()
                    );

                    // 24. Recalcular porcentaje
                    double porcentaje =
                            (double) localidad.getEntradasVendidas() / localidad.getCapacidadMaxima() * 100;

                    localidad.setPorcentajeVenta(porcentaje);

                    // 25. Guardar evento
                    eventoRepo.save(evento);
                }
            }

            else if ("rejected".equals(payment.getStatus())) {

                // 26. Marcar como fallida
                orden.setEstado(EstadoOrden.FALLIDA);
            }

            // 27. Guardar la orden
            ordenRepo.save(orden);

        } catch (Exception e) {

            // 28. Nunca romper webhook
            System.out.println("Error webhook: " + e.getMessage());
        }
    }

    // =========================================================
    // AUXILIAR
    // =========================================================

    /**
     * Convierte un objeto Payment de MercadoPago
     * en un objeto Pago utilizado por el sistema.
     */
    private Pago crearPago(Payment payment) {

        // Convertir el objeto Payment de MercadoPago
        // en un objeto Pago del sistema
        Pago pago = new Pago();

        pago.setCodigo(payment.getId().toString());                             // Id del pago en MercadoPago
        pago.setFecha(payment.getDateCreated().toLocalDateTime());              // Fecha de creación del pago
        pago.setEstado(payment.getStatus());                                    // Estado general (approved, rejected, pending)
        pago.setDetalleEstado(payment.getStatusDetail());                       // Detalle técnico del estado
        pago.setTipoPago(payment.getPaymentTypeId());                           // Tipo de pago (credit_card, debit_card, etc.)
        pago.setMoneda(payment.getCurrencyId());                                // Moneda
        pago.setCodigoAutorizacion(payment.getAuthorizationCode());             // Código de autorización bancaria
        pago.setValorTransaccion(payment.getTransactionAmount().floatValue());  // Valor pagado

        return pago;
    }
}