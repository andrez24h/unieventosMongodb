package dev.andresm.unieventosMongodb.dto.conex;

public record MensajeDTO<T> (

        boolean error,
        T respuesta
) {
}
