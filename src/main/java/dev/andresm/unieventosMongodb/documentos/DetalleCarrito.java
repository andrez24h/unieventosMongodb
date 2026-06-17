package dev.andresm.unieventosMongodb.documentos;

import lombok.*;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder

public class DetalleCarrito implements Serializable {

    // ❌ NO @Id porque es un subdocumento dentro de Carrito
    @Builder.Default
    private String codigoDetalle = UUID.randomUUID().toString();

    private int cantidad;
    private String idEvento;
    private String nombreLocalidad;
}