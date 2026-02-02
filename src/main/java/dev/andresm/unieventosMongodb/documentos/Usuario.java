package dev.andresm.unieventosMongodb.documentos;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder(toBuilder = true)

public class Usuario {

    private String cedula;
    private String direccion;
    private String nombre;
    private List<String> telefonos;
}
