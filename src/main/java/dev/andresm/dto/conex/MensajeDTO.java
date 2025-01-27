package dev.andresm.dto.conex;

public record MensajeDTO<T> (

        boolean error,
        T respuesta
) {
}
