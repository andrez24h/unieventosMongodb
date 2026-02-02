package dev.andresm.unieventosMongodb.documentos;

import lombok.*;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString

public class Carrito {

    // ❌ NO @Id porque NO es documento raíz (va embebido en Cuenta)

    private LocalDateTime fecha = LocalDateTime.now();
    private List<DetalleCarrito> items = new ArrayList<>();
}
