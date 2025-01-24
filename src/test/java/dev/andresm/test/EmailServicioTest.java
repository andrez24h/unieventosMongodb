package dev.andresm.test;

import dev.andresm.dto.email.EmailDTO;
import dev.andresm.servicios.EmailServicio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailServicioTest {

    @Autowired
    EmailServicio emailServicio;

    @Test
    public void enviarEmailTest() throws Exception {

        // Se crea el objeto EmailDTO
        EmailDTO emailDTO = new EmailDTO("Prueba de envío", "Mensaje de prueba 11 ", "amhernandezp@uqvirtual.edu.co");

        // Se invoca el método del servicio de envío de correos y se recibe el booleano
        boolean enviado = emailServicio.enviarEmail(emailDTO);

        // Se verifica que el método de envío retorne "true", es decir, que se haya enviado el correo
        Assertions.assertTrue(enviado, "El correo no fue enviado correctamente");
    }
}
