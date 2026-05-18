package dev.andresm.unieventosMongodb.controladores;

import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import dev.andresm.unieventosMongodb.dto.conex.MensajeDTO;
import dev.andresm.unieventosMongodb.servicios.interfaces.PagoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador encargado de la gestión de pagos.

 * Permite:
 * - Crear preferencias de pago en MercadoPago
 * - Consultar pagos
 * - Procesar webhooks enviados por MercadoPago
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pagos")
public class PagoControlador {

    private final PagoServicio pagoServicio;

    /**
     * Crear preferencia de pago para una orden.
     *
     * @param idOrden identificador de la orden
     * @return preferencia generada
     */
    @PostMapping("/preferencia/{idOrden}")
    public ResponseEntity<MensajeDTO<Preference>> crearPreferenciaPago(
            @PathVariable String idOrden) throws Exception {

        Preference preference = pagoServicio.crearPreferenciaPago(idOrden);

        return ResponseEntity.ok().body(
                new MensajeDTO<>(
                        false,
                        "Preferencia de pago creada correctamente",
                        preference
                )
        );
    }

    /**
     * Consultar información de un pago.
     *
     * @param idPago identificador del pago en MercadoPago
     * @return información del pago
     */
    @GetMapping("/{idPago}")
    public ResponseEntity<MensajeDTO<Payment>> obtenerPago(
            @PathVariable Long idPago) throws Exception {

        Payment payment = pagoServicio.obtenerPago(idPago);

        return ResponseEntity.ok().body(
                new MensajeDTO<>(
                        false,
                        "Pago obtenido correctamente",
                        payment
                )
        );
    }

    /**
     * Endpoint utilizado por MercadoPago para enviar notificaciones.
     *
     * @param request datos enviados por MercadoPago
     */
    @PostMapping("/webhook")
    public void procesarWebhook(
            @RequestBody Map<String, Object> request) {

        pagoServicio.procesarWebhook(request);
    }

}
