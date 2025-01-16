package dev.andresm.servicios;

import dev.andresm.dto.email.EmailDTO;

public interface EmailServicio {

    boolean enviarEmail(EmailDTO emailDTO) throws Exception;
}
