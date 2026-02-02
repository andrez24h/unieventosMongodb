package dev.andresm.unieventosMongodb.dto.evento;

import dev.andresm.unieventosMongodb.documentos.EstadoEvento;
import dev.andresm.unieventosMongodb.documentos.Localidad;
import dev.andresm.unieventosMongodb.documentos.TipoEvento;
import dev.andresm.unieventosMongodb.documentos.Ubicacion;

import java.time.LocalDateTime;
import java.util.List;

public record InformacionEventoDTO(
        String id,
        String nombre,
        String direccion,
        String ciudad,
        String descripcion,
        String imagenPortada,
        String imagenLocalidades,
        TipoEvento tipo,
        EstadoEvento estado,
        Ubicacion ubicacion,
        LocalDateTime fecha,
        List<Localidad> localidades
) {
}
