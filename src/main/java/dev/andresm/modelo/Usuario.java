package dev.andresm.modelo;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder

public class Usuario {

    private String cedula;
    private String nombre;
    private List<String> telefonos;
}
