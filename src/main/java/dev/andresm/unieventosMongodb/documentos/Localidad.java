package dev.andresm.unieventosMongodb.documentos;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder

public class Localidad implements Serializable {

    // - Subdocumento de Evento → NO necesita @Id
    private String nombre;
    private double precio;
    private int entradasVendidas;
    private int capacidadMaxima;
    private double porcentajeVenta;

    // - Método corregido (nunca devuelve negativos)
    public int cantidadDisponible() {
        int disponibles = capacidadMaxima - entradasVendidas;
        return Math.max(disponibles, 0);
    }
}
