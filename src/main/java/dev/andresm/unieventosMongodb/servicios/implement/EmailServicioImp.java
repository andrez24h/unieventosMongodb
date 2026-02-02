package dev.andresm.unieventosMongodb.servicios.implement;

import dev.andresm.unieventosMongodb.dto.email.EmailDTO;
import dev.andresm.unieventosMongodb.servicios.interfaces.EmailServicio;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * =================================================================================
 *               IMPLEMENTACIÓN DEL SERVICIO DE ENVÍO DE CORREOS
 * =================================================================================

 * Esta clase implementa el servicio de envío de emails del sistema.
 * Forma parte de la capa de negocio y se encarga de construir y enviar
 * correos electrónicos utilizando JavaMailSender de Spring.

 * Casos de uso comunes:
 * - Notificaciones del sistema
 * - Confirmaciones de acciones
 * - Recuperación de cuentas
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EmailServicioImp implements EmailServicio {
    /**
     * =================================================================================
     *           DEPENDENCIA: JavaMailSender
     * =================================================================================

     * JavaMailSender es el componente de Spring que permite crear y enviar
     * correos electrónicos utilizando el protocolo SMTP.

     * Es inyectado por Spring mediante inyección de dependencias.
     */
    @Autowired
    private final JavaMailSender javaMailSender;

    /**
     * =================================================================================
     *           ENVÍO DE CORREO ELECTRÓNICO
     * =================================================================================

     * Este método construye y envía un correo electrónico utilizando
     * la información proporcionada en el EmailDTO.
     *
     * @param emailDTO Objeto que contiene:
     *                 - destinatario del correo
     *                 - asunto del correo
     *                 - contenido del mensaje
     *
     * @return true si el correo se envía correctamente,
     *         false si ocurre algún error durante el proceso.
     *
     * @throws Exception si ocurre un error crítico durante la creación
     *                   o envío del correo.
     */
    public boolean enviarEmail (EmailDTO emailDTO) throws Exception {

        /**
         * -----------------------------------------------------------------------------
         *       CREACIÓN DEL MENSAJE
         * -----------------------------------------------------------------------------

         * Se crea un objeto MimeMessage que representa el correo electrónico.
         * Este objeto permite enviar mensajes con contenido HTML.
         */
        MimeMessage mensaje = javaMailSender.createMimeMessage();
        // Se crea un objeto Helper que permite asignar atributos para que construya ese objeto y poder enviarlo
        /**
         * -----------------------------------------------------------------------------
         *  MimeMessageHelper
         * -----------------------------------------------------------------------------
         *
         * MimeMessageHelper facilita la construcción del mensaje,
         * permitiendo asignar de forma sencilla:
         * - destinatario
         * - asunto
         * - contenido
         * - remitente
         */
        MimeMessageHelper helper = new MimeMessageHelper(mensaje);

        try {

            /**
             * -------------------------------------------------------------------------
             *   ASIGNACIÓN DE ATRIBUTOS DEL CORREO
             * -------------------------------------------------------------------------

             Parámetros asignados al mensaje por medio de Helper
             */
            helper.setSubject(emailDTO.asunto());               // Asunto del correo
            helper.setText(emailDTO.contenido(), true);   // Contenido en formato HTML
            helper.setTo(emailDTO.destinatario());              // Destinatario
            helper.setFrom("no_reply@unilocal.com");            // Remitente del sistema

            /**
             * -------------------------------------------------------------------------
             *   ENVÍO DEL CORREO
             * -------------------------------------------------------------------------

             * Se utiliza JavaMailSender para enviar el mensaje construido.
             * Se llama la función de envío "JavaMailSender.send", send recibe el mensaje
                JavaMailSender.send(recibe el MimeMessage)
             */
            javaMailSender.send(mensaje);

            // Si el envío se realiza correctamente, se retorna true
            return true;

        } catch (Exception e) {

            /**
             * -------------------------------------------------------------------------
             *   MANEJO DE ERRORES
             * -------------------------------------------------------------------------

             * Si ocurre un error durante la construcción o el envío del correo,
             * se captura la excepción y se retorna false.
             */
            e.printStackTrace();
        }
        // Retorna false si el correo no pudo ser enviado
        return false;
    }
}



