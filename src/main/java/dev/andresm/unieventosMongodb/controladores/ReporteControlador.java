package dev.andresm.unieventosMongodb.controladores;

import dev.andresm.unieventosMongodb.documentos.Reporte;
import dev.andresm.unieventosMongodb.dto.conex.MensajeDTO;
import dev.andresm.unieventosMongodb.dto.reportes.GenerarReporteDTO;
import dev.andresm.unieventosMongodb.servicios.interfaces.ReporteServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

/**
 * Controlador encargado de la gestión de reportes del sistema.

 * Responsabilidades:
 * - Generar reportes de ventas por evento
 * - Exportar reportes en formato PDF

 * Expone endpoints REST para que el frontend pueda:
 * - Consultar estadísticas de ventas
 * - Descargar reportes en PDF
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reportes")
public class ReporteControlador {

    private final ReporteServicio reporteServicio;

    /**
     * Genera un reporte de ventas para un evento específico.

     * Flujo:
     * 1. Recibe el id del evento desde el frontend.
     * 2. Invoca el servicio de reportes.
     * 3. Retorna el reporte generado.
     *
     * @param generarReporteDTO DTO con el id del evento
     * @return Reporte generado
     * @throws Exception si ocurre un error durante la generación
     */
    @PostMapping("/generar")
    public ResponseEntity<MensajeDTO<Reporte>> generarReporte(
            @RequestBody GenerarReporteDTO generarReporteDTO
    ) throws Exception {

        // Generar reporte
        Reporte reporte = reporteServicio.generarReporte(generarReporteDTO);

        // Retornar respuesta
        return ResponseEntity.ok().body(
                new MensajeDTO<>(
                        false,
                        "Reporte generado correctamente",
                        reporte
                )
        );
    }

    /**
     * Genera y descarga un archivo PDF con la información del reporte.

     * Flujo:
     * 1. Genera el reporte del evento.
     * 2. Construye el archivo PDF en memoria.
     * 3. Retorna el PDF como archivo descargable.
     *
     * @param generarReporteDTO DTO con el id del evento
     * @return Archivo PDF generado
     * @throws Exception si ocurre un error durante la generación
     */
    @PostMapping("/pdf")
    public ResponseEntity<byte[]> generarPDF(
            @RequestBody GenerarReporteDTO generarReporteDTO
    ) throws Exception {

        // 1. Generar reporte
        Reporte reporte = reporteServicio.generarReporte(generarReporteDTO);

        // 2. Crear flujo en memoria
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // 3. Generar PDF
        reporteServicio.generarPDF(reporte, outputStream);

        // 4. Retornar archivo PDF
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=reporte.pdf"
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(outputStream.toByteArray());
    }
}