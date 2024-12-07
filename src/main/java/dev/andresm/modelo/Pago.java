package dev.andresm.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;
import java.time.LocalDateTime;

@Document("pagos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class Pago implements Serializable {

    @Id
    @EqualsAndHashCode.Include
    private String codigo;

    private String codigoAutorizacion;
    private String detalleEstado;
    private String estado;
    private LocalDateTime fecha;
    private String moneda;
    private float valorTransaccion;
    private String tipoPago;
}
