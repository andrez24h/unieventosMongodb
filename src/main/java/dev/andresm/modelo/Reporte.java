package dev.andresm.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class Reporte {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private Evento evento;
    private LocalDateTime fechaGeneracion;
    private double ganancias;
    private double porcentajeVenta;
    private List<Localidad> localidad;
}
