package dev.andresm.unieventosMongodb.documentos;

import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class Localidad {

    // ✔ Subdocumento de Evento → NO necesita @Id
    @EqualsAndHashCode.Include
    private String nombre;

    private double precio;
    private int entradasVendidas;
    private int capacidadMaxima;
    private double porcentajeVenta;

    // ✔ Método corregido (nunca devuelve negativos)
    public int cantidadDisponible() {
        int disponibles = capacidadMaxima - entradasVendidas;
        return Math.max(disponibles, 0);
    }
}
