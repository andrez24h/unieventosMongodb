package dev.andresm.modelo;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class DetalleCarrito {

    @Id
    @EqualsAndHashCode.Include
    private ObjectId codigoDetalle;

    private int cantidad;
    private ObjectId idEvento;
    private String nombreLocalidad;
}
