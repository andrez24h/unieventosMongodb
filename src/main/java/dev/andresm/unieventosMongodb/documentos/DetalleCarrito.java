package dev.andresm.unieventosMongodb.documentos;

import lombok.*;
import org.bson.types.ObjectId;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder

public class DetalleCarrito {

    // ❌ NO @Id porque es un subdocumento dentro de Carrito
    @EqualsAndHashCode.Include
    @Builder.Default
    private String codigoDetalle = UUID.randomUUID().toString();

    private int cantidad;
    private String idEvento;
    private String nombreLocalidad;
}
