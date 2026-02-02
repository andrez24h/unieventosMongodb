package dev.andresm.unieventosMongodb.documentos;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Document("ordenes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class Orden implements Serializable {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String idCliente;
    private String idCupon;
    private String codigoPasarela;
    private Pago pago;
    private double total;
    private LocalDateTime fecha;
    private List<DetalleOrden> items;
}
