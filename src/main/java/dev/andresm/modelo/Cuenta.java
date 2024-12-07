package dev.andresm.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.io.Serializable;
import java.time.LocalDateTime;

@Document("cuentas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString

public class Cuenta implements Serializable {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private Carrito carrito;
    private CodigoValidacion codigoValidacionPassword;
    private CodigoValidacion codigoValidacionRegistro;
    private String email;
    private EstadoCuenta estado;
    private LocalDateTime fechaRegistro;
    private String password;
    private Rol rol;
    private Usuario usuario;
}
