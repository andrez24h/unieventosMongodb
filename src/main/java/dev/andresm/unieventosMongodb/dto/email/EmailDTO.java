package dev.andresm.unieventosMongodb.dto.email;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EmailDTO(

        @NotBlank String asunto,
        @NotBlank String contenido,
        @NotBlank String destinatario
) {}
