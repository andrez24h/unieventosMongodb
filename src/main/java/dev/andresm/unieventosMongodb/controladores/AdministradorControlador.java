package dev.andresm.unieventosMongodb.controladores;

import dev.andresm.unieventosMongodb.documentos.Reporte;
import dev.andresm.unieventosMongodb.dto.conex.MensajeDTO;
import dev.andresm.unieventosMongodb.dto.cupon.ActualizarCuponDTO;
import dev.andresm.unieventosMongodb.dto.cupon.CrearCuponDTO;
import dev.andresm.unieventosMongodb.dto.cupon.ItemCuponDTO;
import dev.andresm.unieventosMongodb.dto.evento.CrearEventoDTO;
import dev.andresm.unieventosMongodb.dto.evento.EditarEventoDTO;
import dev.andresm.unieventosMongodb.dto.evento.ItemEventoDTO;
import dev.andresm.unieventosMongodb.dto.reportes.GenerarReporteDTO;
import dev.andresm.unieventosMongodb.servicios.interfaces.CuponServicio;
import dev.andresm.unieventosMongodb.servicios.interfaces.EventoServicio;
import dev.andresm.unieventosMongodb.servicios.interfaces.ReporteServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * ============================================================================
 * CONTROLADOR DE ADMINISTRADOR
 * ============================================================================

 * Controlador encargado de exponer los endpoints utilizados por los
 * administradores de la plataforma UniEventos.

 * Responsabilidades:
 * - Gestionar eventos
 * - Gestionar cupones
 * - Consultar reportes y estadísticas
 * - Descargar reportes PDF

 * Este controlador NO contiene lógica de negocio.
 * Toda la lógica se delega a los servicios correspondientes.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdministradorControlador {

    private final CuponServicio cuponServicio;
    private final EventoServicio eventoServicio;
    private final ReporteServicio reporteServicio;

    // =========================================================================
    // EVENTOS
    // =========================================================================

    /**
     * Crea un nuevo evento.
     *
     * @param crearEventoDTO información del evento
     * @return id del evento creado
     * @throws Exception si ocurre un error en la creación
     */
    @PostMapping("/crear-evento")
    public ResponseEntity<MensajeDTO<String>> crearEvento(
            @Valid @RequestBody CrearEventoDTO crearEventoDTO
    ) throws Exception {

        String idEvento = eventoServicio.crearEvento(crearEventoDTO);

        return ResponseEntity.ok(
                new MensajeDTO<>(false,
                        "Evento creado exitosamente",
                        idEvento)
        );
    }

    /**
     * Actualiza la información de un evento existente.
     *
     * @param editarEventoDTO datos actualizados del evento
     * @return id del evento actualizado
     * @throws Exception si el evento no existe
     */
    @PutMapping("/editar-evento")
    public ResponseEntity<MensajeDTO<String>> editarEvento(
            @Valid @RequestBody EditarEventoDTO editarEventoDTO
    ) throws Exception {

        String idEvento = eventoServicio.editarEvento(editarEventoDTO);

        return ResponseEntity.ok(
                new MensajeDTO<>(false,
                        "Evento actualizado exitosamente",
                        idEvento)
        );
    }

    /**
     * Elimina un evento del sistema.
     *
     * @param id identificador del evento
     * @return mensaje de confirmación
     * @throws Exception si el evento no existe
     */
    @DeleteMapping("/eliminar-evento/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarEvento(
            @PathVariable String id
    ) throws Exception {

        String resultado = eventoServicio.eliminarEvento(id);

        return ResponseEntity.ok(
                new MensajeDTO<>(false,
                        "Evento eliminado exitosamente",
                        resultado)
        );
    }

    /**
     * Lista todos los eventos registrados.
     *
     * @return lista de eventos
     */
    @GetMapping("/listar-eventos")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> listarEventos() {

        List<ItemEventoDTO> eventos = eventoServicio.listarEventos();

        return ResponseEntity.ok(
                new MensajeDTO<>(false,
                        "Lista de eventos",
                        eventos)
        );
    }

    // =========================================================================
    // CUPONES
    // =========================================================================

    /**
     * Crea un nuevo cupón.
     *
     * @param crearCuponDTO información del cupón
     * @return código del cupón creado
     * @throws Exception si ocurre error en la creación
     */
    @PostMapping("/crear-cupon")
    public ResponseEntity<MensajeDTO<String>> crearCupon(
            @Valid @RequestBody CrearCuponDTO crearCuponDTO
    ) throws Exception {

        String codigo = cuponServicio.crearCupon(crearCuponDTO);

        return ResponseEntity.ok(
                new MensajeDTO<>(false,
                        "Cupón creado exitosamente",
                        codigo)
        );
    }

    /**
     * Actualiza un cupón existente.
     *
     * @param actualizarCuponDTO datos actualizados
     * @return id del cupón actualizado
     * @throws Exception si el cupón no existe
     */
    @PutMapping("/actualizar-cupon")
    public ResponseEntity<MensajeDTO<String>> actualizarCupon(
            @Valid @RequestBody ActualizarCuponDTO actualizarCuponDTO
    ) throws Exception {

        String idCupon = cuponServicio.actualizarCupon(actualizarCuponDTO);

        return ResponseEntity.ok(
                new MensajeDTO<>(false,
                        "Cupón actualizado exitosamente",
                        idCupon)
        );
    }

    /**
     * Elimina un cupón del sistema.
     *
     * @param idCupon identificador del cupón
     * @return mensaje de confirmación
     * @throws Exception si el cupón no existe
     */
    @DeleteMapping("/eliminar-cupon/{idCupon}")
    public ResponseEntity<MensajeDTO<String>> eliminarCupon(
            @PathVariable String idCupon
    ) throws Exception {

        cuponServicio.borrarCupon(idCupon);

        return ResponseEntity.ok(
                new MensajeDTO<>(false,
                        "Cupón eliminado exitosamente",
                        idCupon)
        );
    }

    /**
     * Lista los cupones disponibles.
     *
     * @return lista de cupones
     */
    @GetMapping("/listar-cupones")
    public ResponseEntity<MensajeDTO<List<ItemCuponDTO>>> listarCupones() {

        List<ItemCuponDTO> cupones = cuponServicio.listarCupones();

        return ResponseEntity.ok(
                new MensajeDTO<>(false,
                        "Lista de cupones",
                        cupones)
        );
    }

    // =========================================================================
    // REPORTES
    // =========================================================================

    /**
     * Genera estadísticas de ventas de un evento.
     *
     * @param idEvento identificador del evento
     * @return reporte generado
     * @throws Exception si el evento no existe
     */
    @GetMapping("/reportes/{idEvento}")
    public ResponseEntity<MensajeDTO<Reporte>> generarReporte(
            @PathVariable String idEvento
    ) throws Exception {

        Reporte reporte = reporteServicio.generarReporte(
                new GenerarReporteDTO(idEvento)
        );

        return ResponseEntity.ok(
                new MensajeDTO<>(false,
                        "Reporte generado exitosamente",
                        reporte)
        );
    }

    /**
     * Descarga el reporte PDF de un evento.
     *
     * @param idEvento identificador del evento
     * @return archivo PDF
     * @throws Exception si ocurre error en la generación
     */
    @GetMapping("/reportes/pdf/{idEvento}")
    public ResponseEntity<byte[]> descargarReportePDF(
            @PathVariable String idEvento
    ) throws Exception {

        // Generar reporte
        Reporte reporte = reporteServicio.generarReporte(
                new GenerarReporteDTO(idEvento)
        );

        // Crear flujo PDF
        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        // Generar PDF
        reporteServicio.generarPDF(reporte, outputStream);

        // Convertir a bytes
        byte[] pdf = outputStream.toByteArray();

        // Configurar headers
        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_PDF);

        headers.setContentDispositionFormData(
                "attachment",
                "reporte_" + idEvento + ".pdf"
        );

        // Retornar archivo
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(pdf);
    }
}