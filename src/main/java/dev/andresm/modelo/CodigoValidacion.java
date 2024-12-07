package dev.andresm.modelo;

import lombok.*;
import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class CodigoValidacion {

    @Id
    @EqualsAndHashCode.Include
    private String codigo;

    private LocalDateTime fechaCreacion;
}
