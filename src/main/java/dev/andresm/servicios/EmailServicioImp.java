package dev.andresm.servicios;

import dev.andresm.dto.email.EmailDTO;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmailServicioImp implements EmailServicio {

    @Autowired
    private final JavaMailSender javaMailSender;

    public boolean enviarEmail (EmailDTO emailDTO) throws Exception {

        MimeMessage mensaje = javaMailSender.createMimeMessage();
        // Se crea un objeto Helper que permite asignar atributos para que construya ese objeto y poder enviarlo

        MimeMessageHelper helper = new MimeMessageHelper(mensaje);

        try {

            // Parámetros asignados al mensaje por medio de Helper
            helper.setSubject(emailDTO.asunto());
            helper.setText(emailDTO.contenido(), true);
            helper.setTo(emailDTO.destinatario());
            helper.setFrom("no_reply@unilocal.com");

            // Se llama la función de envío "JavaMailSender.send" , send recibe el mensaje
            // JavaMailSender.send(recibe el MimeMessage)
            javaMailSender.send(mensaje);

            return true; // Retornar true si el correo se envía correctamente

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false; // Retornar false si ocurre algún error
    }
}



