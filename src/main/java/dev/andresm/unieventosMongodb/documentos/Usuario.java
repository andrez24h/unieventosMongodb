package dev.andresm.unieventosMongodb.documentos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder(toBuilder = true)

public class Usuario implements Serializable {

    @NotBlank
    private String cedula;

    @NotBlank
    private String direccion;

    @NotBlank
    private String nombre;

    @NotEmpty
    private List<@NotBlank String> telefonos;
}
