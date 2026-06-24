package dev.andresm.unieventosMongodb.documentos;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Ubicacion implements Serializable {

    private double latitud;
    private double longitud;
}
