package dev.andresm.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString

public class Carrito {

    @Id
    @EqualsAndHashCode.Include
    private String idCarrito;

    private LocalDateTime fecha;
    private List<DetalleCarrito> items;
}
