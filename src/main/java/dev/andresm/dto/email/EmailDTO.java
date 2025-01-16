package dev.andresm.dto.email;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record EmailDTO(

        @NotBlank String asunto,
        String contenido,
        @NotBlank String destinatario) {
}
