package dev.andresm.unieventosMongodb.servicios.implement;

import com.itextpdf.layout.element.Paragraph;
import dev.andresm.unieventosMongodb.documentos.*;
import dev.andresm.unieventosMongodb.dto.reportes.GenerarReporteDTO;
import dev.andresm.unieventosMongodb.repositorios.EventoRepo;
import dev.andresm.unieventosMongodb.repositorios.OrdenRepo;
import dev.andresm.unieventosMongodb.servicios.interfaces.ReporteServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;

import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de reportes.

 * Responsabilidades:
 * - Generar reportes de ventas por evento
 * - Calcular métricas de ocupación y ganancias
 * - Generar documentos PDF con la información del reporte

 * IMPORTANTE:
 * Este servicio es responsable de obtener los datos necesarios
 * (evento y órdenes) desde los repositorios.

 * No recibe entidades completas desde el exterior,
 * evitando acoplamiento innecesario.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class ReporteServicioImp implements ReporteServicio {

    private final EventoRepo eventoRepo;
    private final OrdenRepo ordenRepo;

    /**
     * Genera un reporte de ventas para un evento específico.
     * <p>
     * Flujo:
     * 1. Obtener el evento
     * 2. Obtener órdenes asociadas
     * 3. Filtrar órdenes pagadas
     * 4. Calcular ventas por localidad
     * 5. Calcular ganancias totales
     * 6. Calcular porcentaje de ocupación
     * 7. Construir objeto Reporte
     *
     * @param generarReporteDTO contiene el id del evento
     * @return reporte generado
     * @throws Exception si el evento no existe
     */
    @Override
    public Reporte generarReporte(GenerarReporteDTO generarReporteDTO) throws Exception {

        // 1. Buscar evento
        String idEvento = generarReporteDTO.idEvento();

        // 2. Buscar evento usando Optional
        // Si no existe, se lanza una excepción para evitar continuar el flujo
        Optional<Evento> optionalEvento = eventoRepo.buscarId(idEvento);

        /**
         * Evento evento = eventoRepo.buscarId(idEvento)
         *         .orElseThrow(() -> new Exception("Evento no encontrado"));
         */

        if (optionalEvento.isEmpty()) {
            throw new Exception("Evento no encontrado");
        }

        // 2.1. Obtener el evento desde el Optional
        // En este punto ya se validó que el Optional contiene un valor,
        // por lo tanto es seguro utilizar get()
        Evento evento = optionalEvento.get();

        // 3. Obtener órdenes completas del evento
        List<Orden> ordenes = ordenRepo.buscarOrdenesPorEvento(idEvento);

        // 4. Crear reporte
        Reporte reporte = new Reporte();
        reporte.setEvento(evento);
        reporte.setFechaGeneracion(LocalDateTime.now());

        double totalGanancias = 0;
        List<Localidad> localidadReporte = new ArrayList<>();

        // 5. Recorrer localidades del evento
        for (Localidad localidad : evento.getLocalidades()) {

            int totalVendido = 0;

            // 6. Recorrer órdenes
            for (Orden orden : ordenes) {

                // 7. Recorrer los ítems (detalles) de cada orden
                // Cada detalle representa una compra de una localidad específica
                for (DetalleOrden detalle : orden.getItems()) {

                    // Validar si el detalle corresponde a la localidad actual
                    // Se compara por nombre de localidad
                    if (detalle.getNombreLocalidad().equals(localidad.getNombre())) {

                        // Acumular cantidad de entradas vendidas
                        totalVendido += detalle.getCantidad();

                        // Acumular ganancias generadas por esas entradas
                        totalGanancias +=
                                detalle.getCantidad() * detalle.getPrecioUnitario();
                    }
                }
            }

            // 8. Calcular porcentaje de venta
            // Fórmula: (entradas vendidas / capacidad máxima) * 100
            double porcentajeVenta =
                    (double) totalVendido / localidad.getCapacidadMaxima() * 100;

            // Asignar porcentaje calculado a la localidad
            localidad.setPorcentajeVenta(porcentajeVenta);

            // Agregar la localidad procesada al reporte final
            localidadReporte.add(localidad);
        }

        // 9. Asignar resultados al reporte
        reporte.setLocalidad(localidadReporte);
        reporte.setGanancias(totalGanancias);

        // 10. Calcular porcentaje promedio total
        double porcentajeTotal = 0;

        if (!localidadReporte.isEmpty()) {
            double suma = 0;

            for (Localidad localidad : localidadReporte) {
                suma += localidad.getPorcentajeVenta();
            }
            porcentajeTotal = suma / localidadReporte.size();
        }
        reporte.setPorcentajeVenta(porcentajeTotal);

        return reporte;
    }

    /**
     * Genera un archivo PDF con la información del reporte.

     * Flujo:
     * 1. Crear escritor del PDF
     * 2. Inicializar documento PDF
     * 3. Crear objeto Document para escritura
     * 4. Agregar información general del reporte
     * 5. Agregar sección de detalle por localidad
     * 6. Recorrer localidades y agregar información
     * 7. Cerrar documento
     *
     * @param reporte objeto con la información calculada
     * @param outputStream flujo de salida donde se escribirá el PDF
     * @throws Exception si ocurre un error en la generación
     */
    @Override
    public void generarPDF(Reporte reporte, OutputStream outputStream) throws Exception {

        // 1. Crear escritor del PDF (encargado de escribir en el OutputStream)
        PdfWriter writer = new PdfWriter(outputStream);

        // 2. Inicializar el documento PDF
        PdfDocument pdfDocument = new PdfDocument(writer);

        // 3. Crear el documento para agregar contenido
        Document document = new Document(pdfDocument);

        // =========================================================
        // INFORMACIÓN GENERAL
        // =========================================================

        // 4. Agregar datos principales del reporte
        document.add(new Paragraph("Reporte de Evento: " + reporte.getEvento().getNombre()));
        document.add(new Paragraph("Fecha de Generación: " + reporte.getFechaGeneracion()));
        document.add(new Paragraph("Porcentaje de Venta Total: " + reporte.getPorcentajeVenta() + "%"));
        document.add(new Paragraph("Ganancias Totales: $" + reporte.getGanancias()));

        // =========================================================
        // DETALLE POR LOCALIDAD
        // =========================================================

        // 5. Título de la sección
        document.add(new Paragraph("Detalle por Localidad:"));

        // 6. Recorrer localidades del reporte
        for (Localidad localidad : reporte.getLocalidad()) {

            // Agregar información de cada localidad
            document.add(new Paragraph(
                    localidad.getNombre() + ": " +
                            localidad.getPorcentajeVenta() + "% vendido"
            ));
        }
        // 7. Cerrar documento (muy importante para guardar el archivo correctamente)
        document.close();
    }
}

