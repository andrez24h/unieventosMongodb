package dev.andresm.unieventosMongodb.documentos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Ubicacion {

    private double latitud;
    private double longitud;
}
