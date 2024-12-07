package dev.andresm.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class Localidad {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String nombre;
    private double precio;
    private int entradasVendidas;
    private int capacidadMaxima;
    private double porcentajeVenta;

    public int cantidadDisponible() {
        return capacidadMaxima - entradasVendidas;
    }
}
