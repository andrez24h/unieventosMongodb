package dev.andresm.unieventosMongodb.documentos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class DetalleOrden {

    @EqualsAndHashCode.Include
    private String idEvento;

    private int cantidad;
    private double precioUnitario;
    private String nombreLocalidad;
}
