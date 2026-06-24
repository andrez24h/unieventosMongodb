package dev.andresm.unieventosMongodb.documentos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class DetalleOrden {

    private String idEvento;
    private int cantidad;
    private double precioUnitario;
    private String nombreLocalidad;
}
