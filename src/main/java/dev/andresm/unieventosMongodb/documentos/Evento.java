package dev.andresm.unieventosMongodb.documentos;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Document("eventos")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class Evento implements Serializable {

    @Id
    @EqualsAndHashCode.Include
    private String id;
    private String idUsuario;
    private String nombre;
    private String descripcion;
    private Ubicacion ubicacion;
    private String direccion;
    private String ciudad;
    private String imagenPortada;
    private String imagenLocalidades;
    private EstadoEvento estado;
    private TipoEvento tipo;
    private LocalDateTime fecha;
    private List<Localidad> localidades = new ArrayList<>();


    @Builder
    public Evento(String idUsuario, String nombre, String descripcion, Ubicacion ubicacion, String direccion, String ciudad, String imagenPortada, String imagenLocalidades, EstadoEvento estado, TipoEvento tipo, LocalDateTime fecha, List<Localidad> localidades) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.imagenPortada = imagenPortada;
        this.imagenLocalidades = imagenLocalidades;
        this.estado = estado;
        this.tipo = tipo;
        this.fecha = fecha;
        this.localidades = localidades;
    }
}

/*
@Override
public String crearEvento(CrearEventoDTO dto) throws Exception {

    if (existeNombre(dto.nombre())) {
        throw new Exception("El nombre ya existe, elija otro nombre");
    }

    Cuenta cuenta = cuentaRepo.findById(dto.idUsuarion())
            .orElseThrow(() -> new Exception("La cuenta no existe"));

    Evento evento = Evento.builder()
            .idUsuario(cuenta.getId())
            .nombre(dto.nombre())
            .descripcion(dto.descripcion())
            .direccion(dto.direccion())
            .ciudad(dto.ciudad())
            .imagenPortada(dto.imagenPortada())
            .imagenLocalidades(dto.imagenLocalidades())
            .estado(dto.estado())
            .tipo(dto.tipo())
            .fecha(dto.fecha())
            .localidades(dto.localidades())
            .ubicacion(dto.ubicacion())
            .build();

    eventoRepo.save(evento);

    return evento.getId();
}
*/


