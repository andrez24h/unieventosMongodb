package dev.andresm.unieventosMongodb.documentos;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Document("cupones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder

public class Cupon implements Serializable {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private List<String> beneficiarios;
    private String codigo;
    private double descuento;
    private EstadoCupon estado;
    private LocalDateTime fechaVencimiento;
    private String nombre;
    private TipoCupon tipo;
}
