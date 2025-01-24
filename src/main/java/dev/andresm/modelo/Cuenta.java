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
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString

    // Clase sin Constructor con los todos los atributos.
    // En el Builder no está el id, yá que este código es autogenerado por la bd.
    // Estado de la Cuenta es inicializado INACTIVO
public class Cuenta implements Serializable {

    @Id
    @EqualsAndHashCode.Include
    private String id;  // MongoDB genera automáticamente este valor como un ObjectId y
                        // Spring Data MongoDB lo convierte a una representación en String.

    private Carrito carrito;
    private CodigoValidacion codigoValidacionPassword;
    private CodigoValidacion codigoValidacionRegistro;
    private String email;
    private EstadoCuenta estado = EstadoCuenta.INACTIVO;
    private LocalDateTime fechaRegistro;

    @ToString.Exclude
    private String password;
    private Rol rol;
    private Usuario usuario;

    @Builder(toBuilder = true)
    public Cuenta(String id, Carrito carrito, CodigoValidacion codigoValidacionPassword, CodigoValidacion codigoValidacionRegistro,
                  String email, EstadoCuenta estado, LocalDateTime fechaRegistro, String password, Rol rol, Usuario usuario) {
        this.id = id;
        this.carrito = carrito;
        this.codigoValidacionPassword = codigoValidacionPassword;
        this.codigoValidacionRegistro = codigoValidacionRegistro;
        this.email = email;
        this.estado = estado;
        this.fechaRegistro = fechaRegistro;
        this.password = password;
        this.rol = rol;
        this.usuario = usuario;
    }
}
