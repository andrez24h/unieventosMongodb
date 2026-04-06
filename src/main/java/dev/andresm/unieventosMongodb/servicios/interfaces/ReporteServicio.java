package dev.andresm.unieventosMongodb.servicios.interfaces;

import dev.andresm.unieventosMongodb.documentos.Reporte;
import dev.andresm.unieventosMongodb.dto.reportes.GenerarReporteDTO;

import java.io.OutputStream;

/**
 * Servicio encargado de la generación de reportes del sistema.

 * Responsabilidades:
 * - Generar reportes de ventas por evento
 * - Calcular estadísticas (ganancias, porcentajes)
 * - Exportar reportes en formato PDF
 */
public interface ReporteServicio {

    /**
     * Genera un reporte completo de un evento.

     * Flujo:
     * - Obtiene el evento desde la base de datos
     * - Consulta las órdenes asociadas
     * - Calcula ventas por localidad
     * - Calcula ganancias totales
     * - Calcula porcentaje de ocupación

     * @param generarReporteDTO DTO con el id del evento
     * @return Reporte generado
     * @throws Exception si el evento no existe
     */
    Reporte generarReporte(GenerarReporteDTO generarReporteDTO) throws Exception;

    /**
     * Genera un archivo PDF a partir de un reporte.

     * @param reporte objeto con la información calculada
     * @param outputStream flujo de salida del archivo PDF
     * @throws Exception si ocurre un error al generar el documento
     */
    void generarPDF(Reporte reporte, OutputStream outputStream) throws Exception;

}
