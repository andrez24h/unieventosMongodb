package dev.andresm.unieventosMongodb.controladores;

import dev.andresm.unieventosMongodb.dto.conex.MensajeDTO;
import dev.andresm.unieventosMongodb.servicios.interfaces.ImagenesServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador encargado de la gestión de imágenes.

 * Permite:
 * - Subir imágenes a Firebase Storage
 * - Eliminar imágenes almacenadas
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/imagenes")
public class ImagenesControlador {

    private final ImagenesServicio imagenesServicio;

    /**
     * Subir una imagen al almacenamiento.
     *
     * @param imagen archivo enviado desde el cliente
     * @return URL pública de la imagen
     */
    @PostMapping("/subir")
    public ResponseEntity<MensajeDTO<String>> subirImagen(
            @RequestParam("imagen") MultipartFile imagen) {

        try {

            String url = imagenesServicio.subirImagen(imagen);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new MensajeDTO<>(
                            false,
                            "Imagen subida correctamente",
                            url
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new MensajeDTO<>(
                            true,
                            e.getMessage(),
                            null
                    )
            );
        }
    }

    /**
     * Eliminar una imagen del almacenamiento.
     *
     * @param nombreImagen nombre del archivo almacenado
     * @return mensaje de confirmación
     */
    @DeleteMapping("/eliminar/{nombreImagen}")
    public ResponseEntity<MensajeDTO<String>> eliminarImagen(
            @PathVariable String nombreImagen) {

        try {

            imagenesServicio.eliminarImagen(nombreImagen);

            return ResponseEntity.status(HttpStatus.OK).body(
                    new MensajeDTO<>(
                            false,
                            "Imagen eliminada correctamente",
                            nombreImagen
                    )
            );

        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new MensajeDTO<>(
                            true,
                            e.getMessage(),
                            null
                    )
            );
        }
    }
}