package dev.andresm.modelo;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

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
