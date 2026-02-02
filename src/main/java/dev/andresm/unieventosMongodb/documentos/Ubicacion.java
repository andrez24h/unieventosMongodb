package dev.andresm.unieventosMongodb.documentos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Ubicacion {
    private double latitud;
    private double longitud;
}
