package dev.andresm.unieventosMongodb.documentos;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

/**
 * Representa un reporte generado para un evento en la plataforma UniEventos.

 * Contiene información consolidada sobre:
 * - El evento analizado
 * - Fecha de generación del reporte
 * - Ganancias totales generadas
 * - Porcentaje de ocupación global
 * - Detalle de ventas por localidad

 * Este objeto es utilizado principalmente para:
 * - Visualización de estadísticas
 * - Generación de archivos PDF
 * - Análisis de rendimiento de eventos
 */
public class Reporte {

    /**
     * Identificador único del reporte.
     */
    @Id
    @EqualsAndHashCode.Include
    private String id;

    /**
     * Evento sobre el cual se genera el reporte.
     */
    private Evento evento;

    /**
     * Fecha y hora en que se generó el reporte.
     */
    private LocalDateTime fechaGeneracion;

    /**
     * Ganancias totales generadas por las ventas del evento.
     */
    private double ganancias;

    /**
     * Porcentaje promedio de ocupación del evento.
     */
    private double porcentajeVenta;

    /**
     * Lista de localidades con su porcentaje de ocupación.
     */
    private List<Localidad> localidad;
}
