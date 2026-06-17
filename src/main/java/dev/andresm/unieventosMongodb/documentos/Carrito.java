package dev.andresm.unieventosMongodb.documentos;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString

public class Carrito implements Serializable {

    // ❌ NO @Id porque NO es documento raíz (va embebido en Cuenta)
    // ❌ NO @AllArgsConstructor porque en el servicio se inicializa vacío new Carrito()

    private LocalDateTime fecha = LocalDateTime.now();
    private List<DetalleCarrito> items = new ArrayList<>();
}
