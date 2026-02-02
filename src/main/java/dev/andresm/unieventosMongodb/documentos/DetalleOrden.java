package dev.andresm.unieventosMongodb.documentos;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class DetalleOrden {

    @Id
    @EqualsAndHashCode.Include
    private ObjectId idEvento;

    private int cantidad;
    private double precioUnitario;
    private String nombreLocalidad;
}
