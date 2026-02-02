package dev.andresm.unieventosMongodb.servicios.interfaces;

import dev.andresm.unieventosMongodb.dto.email.EmailDTO;

/**
 * 🔹 Servicio de envío de correos electrónicos.
 * Define las operaciones relacionadas con el envío de emails
 * para notificaciones del sistema, validaciones y recuperación
 * de información de cuentas.
 */
public interface EmailServicio {

    /**
     * 🔹 Enviar un correo electrónico.
     *
     * @param emailDTO datos necesarios para el envío del correo
     *                 (destinatario, asunto y contenido)
     * @return true si el correo se envía correctamente
     * @throws Exception si ocurre un error durante el envío
     */
    boolean enviarEmail(EmailDTO emailDTO) throws Exception;
}
