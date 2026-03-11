package dev.andresm.unieventosMongodb.excepciones;

import dev.andresm.unieventosMongodb.dto.conex.MensajeDTO;
import dev.andresm.unieventosMongodb.dto.conex.ValidacionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * Manejador global de excepciones para toda la aplicación.

 * Esta clase permite capturar y centralizar los errores generados
 * en los controladores REST, evitando repetir bloques try-catch.

 * Funciones principales:
 * - Capturar excepciones generales del sistema.
 * - Capturar errores de validación (@Valid).
 * - Retornar respuestas estructuradas usando MensajeDTO.

 * Anotación:
 * @RestControllerAdvice permite interceptar excepciones
 * lanzadas por cualquier controlador REST.
 */
@RestControllerAdvice
public class GlobalException {

    /**
     * Captura cualquier excepción no controlada.
     *
     * @param e excepción lanzada
     * @return respuesta HTTP 500 con mensaje de error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<MensajeDTO<String>> generalException(Exception e){

        return ResponseEntity
                .internalServerError()
                .body( new MensajeDTO<>(true, e.getMessage()));
    }

    /**
     * Captura errores de validación generados por anotaciones
     * como @NotBlank, @Size, @NotNull, etc.

     * Se activa cuando un DTO anotado con @Valid falla.
     *
     * @param ex excepción de validación
     * @return respuesta HTTP 400 con lista de errores por campo
     */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MensajeDTO<List<ValidacionDTO>>> validationException(

            MethodArgumentNotValidException ex ) {

        List<ValidacionDTO> errores = new ArrayList<>();
        BindingResult results = ex.getBindingResult();

        for (FieldError e: results.getFieldErrors()) {

            errores.add( new ValidacionDTO(
                    e.getField(),
                    e.getDefaultMessage()));
        }

        return ResponseEntity.
                badRequest()
                .body( new MensajeDTO<>(true, errores));
    }
}
