package dev.andresm.unieventosMongodb.dto.evento;

import dev.andresm.unieventosMongodb.documentos.EstadoEvento;
import dev.andresm.unieventosMongodb.documentos.Localidad;
import dev.andresm.unieventosMongodb.documentos.TipoEvento;
import dev.andresm.unieventosMongodb.documentos.Ubicacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public record CrearEventoDTO(
        @NotBlank String idUsuario,
        @NotBlank String nombre,
        @NotBlank String direccion,
        @NotBlank String ciudad,
        @NotBlank String descripcion,
        @NotBlank String imagenPortada,
        @NotBlank String imagenLocalidades,
        @NotNull TipoEvento tipo,
        @NotNull EstadoEvento estado,
        @NotNull Ubicacion ubicacion,
        @NotNull LocalDateTime fecha,
        @NotNull List<Localidad> localidades
        ) {
}

