package dev.andresm.unieventosMongodb.controladores;

import dev.andresm.unieventosMongodb.documentos.Evento;
import dev.andresm.unieventosMongodb.documentos.TipoEvento;
import dev.andresm.unieventosMongodb.dto.conex.MensajeDTO;
import dev.andresm.unieventosMongodb.dto.evento.*;
import dev.andresm.unieventosMongodb.servicios.interfaces.EventoServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * =================================================================================
 *  CONTROLADOR DE EVENTOS
 * =================================================================================

 * Expone los endpoints REST relacionados con:
 * - Creación de eventos
 * - Edición y eliminación
 * - Consulta de información
 * - Disponibilidad de localidades
 * - Listado y filtros de búsqueda
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/evento")
public class EventoControlador {

    private final EventoServicio eventoServicio;

    /* =============================================================================
     *  CREAR EVENTO
     * =============================================================================
     */

    @PostMapping("/crear")
    public ResponseEntity<MensajeDTO<String>> crearEvento(
            @Valid @RequestBody CrearEventoDTO crearEventoDTO
    ) throws Exception {

        String id = eventoServicio.crearEvento(crearEventoDTO);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Evento creado correctamente",
                        id
                )
        );
    }

    /* =============================================================================
     *  EDITAR EVENTO
     * =============================================================================
     */

    @PutMapping("/editar")
    public ResponseEntity<MensajeDTO<String>> editarEvento(
            @Valid @RequestBody EditarEventoDTO editarEventoDTO
    ) throws Exception {

        String id = eventoServicio.editarEvento(editarEventoDTO);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Evento editado correctamente",
                        id
                )
        );
    }

    /* =============================================================================
     *  ELIMINAR EVENTO
     * =============================================================================
     */

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarEvento(
            @PathVariable String id
    ) throws Exception {

        eventoServicio.eliminarEvento(id);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Evento eliminado correctamente",
                        id
                )
        );
    }

    /* =============================================================================
     *  OBTENER INFORMACIÓN DETALLADA
     * =============================================================================
     */

    @GetMapping("/detalle/{id}")
    public ResponseEntity<MensajeDTO<InformacionEventoDTO>> obtenerInformacionEvento(
            @PathVariable String id
    ) throws Exception {

        InformacionEventoDTO informacion =
                eventoServicio.obtenerInformacionEvento(id);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Información del evento obtenida correctamente",
                        informacion
                )
        );
    }

    /* =============================================================================
     *  LISTAR TODOS LOS EVENTOS
     * =============================================================================
     */

    @GetMapping("/listar")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> listarEventos() {

        List<ItemEventoDTO> lista = eventoServicio.listarEventos();

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Lista de eventos obtenida correctamente",
                        lista
                )
        );
    }

    /* =============================================================================
     *  LISTAR EVENTOS DISPONIBLES PARA CLIENTES
     * =============================================================================
     */

    @GetMapping("/listar-cliente")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> listarEventosCliente() {

        List<ItemEventoDTO> lista = eventoServicio.listarEventosCliente();

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Eventos disponibles obtenidos correctamente",
                        lista
                )
        );
    }

    /* =============================================================================
     *  VERIFICAR DISPONIBILIDAD
     * =============================================================================
     */

    @PostMapping("/disponibilidad")
    public ResponseEntity<MensajeDTO<Boolean>> disponibilidad(
            @Valid @RequestBody DisponibilidadEventoDTO disponibilidadEventoDTO
    ) throws Exception {

        boolean disponible =
                eventoServicio.disponibilidad(disponibilidadEventoDTO);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Disponibilidad verificada correctamente",
                        disponible
                )
        );
    }

    /* =============================================================================
     *  OBTENER EVENTO POR ID
     * =============================================================================
     */

    @GetMapping("/obtener/{id}")
    public ResponseEntity<MensajeDTO<Evento>> obtenerEvento(
            @PathVariable String id
    ) throws Exception {

        Evento evento = eventoServicio.obtenerEvento(id);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Evento obtenido correctamente",
                        evento
                )
        );
    }

    /* =============================================================================
     *  FILTRO DINÁMICO
     * =============================================================================
     */

    @PostMapping("/filtrar")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> filtrarEventos(
            @RequestBody FiltroEventoDTO filtroEventoDTO
    ) {

        List<ItemEventoDTO> lista =
                eventoServicio.filtrarEventos(filtroEventoDTO);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Eventos filtrados correctamente",
                        lista
                )
        );
    }

    /* =============================================================================
     *  FILTRAR POR CIUDAD
     * =============================================================================
     */

    @GetMapping("/filtrar-ciudad/{ciudad}")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> filtrarEventosPorCiudad(
            @PathVariable String ciudad
    ) throws Exception {

        List<ItemEventoDTO> lista =
                eventoServicio.filtrarEventosPorCiudad(ciudad);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Eventos filtrados por ciudad correctamente",
                        lista
                )
        );
    }

    /* =============================================================================
     *  FILTRAR POR TIPO
     * =============================================================================
     */

    @GetMapping("/filtrar-tipo/{tipo}")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> filtrarEventosPorTipo(
            @PathVariable TipoEvento tipo
    ) {

        List<ItemEventoDTO> lista =
                eventoServicio.filtrarEventosPorTipo(tipo);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Eventos filtrados por tipo correctamente",
                        lista
                )
        );
    }

    /* =============================================================================
     *  FILTRAR POR NOMBRE
     * =============================================================================
     */

    @GetMapping("/filtrar-nombre/{nombre}")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> filtrarEventosPorNombre(
            @PathVariable String nombre
    ) {

        List<ItemEventoDTO> lista =
                eventoServicio.filtrarEventosPorNombre(nombre);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Eventos filtrados por nombre correctamente",
                        lista
                )
        );
    }

    /* =============================================================================
     *  FILTRAR POR NOMBRE Y CIUDAD
     * =============================================================================
     */

    @PostMapping("/filtrar-nombre-ciudad")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> filtrarEventosPorNombreYCiudad(
            @RequestBody FiltrarPorNombreYCiudadDTO dto
    ) {

        List<ItemEventoDTO> lista =
                eventoServicio.filtrarEventosPorNombreYCiudad(dto);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Eventos filtrados por nombre y ciudad correctamente",
                        lista
                )
        );
    }

    /* =============================================================================
     *  FILTRAR POR FECHA
     * =============================================================================
     */

    @PostMapping("/filtrar-fecha")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> filtrarEventosPorFecha(
            @RequestBody FiltrarPorFechaDTO dto
    ) {

        List<ItemEventoDTO> lista =
                eventoServicio.filtrarEventosPorFecha(dto);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Eventos filtrados por fecha correctamente",
                        lista
                )
        );
    }

    /* =============================================================================
     *  FILTRAR POR FECHA Y CIUDAD
     * =============================================================================
     */

    @PostMapping("/filtrar-fecha-ciudad")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> filtrarEventosPorFechaYCiudad(
            @RequestBody FiltrarPorFechaYCiudadDTO dto
    ) {

        List<ItemEventoDTO> lista =
                eventoServicio.filtrarEventosPorFechaYCiudad(dto);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Eventos filtrados por fecha y ciudad correctamente",
                        lista
                )
        );
    }

    /* =============================================================================
     *  FILTRAR POR FECHA Y TIPO
     * =============================================================================
     */

    @PostMapping("/filtrar-fecha-tipo")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> filtrarEventosPorFechaYTipo(
            @RequestBody FiltrarPorFechaYTipoDTO dto
    ) {

        List<ItemEventoDTO> lista =
                eventoServicio.filtrarEventosPorFechaYTipo(dto);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Eventos filtrados por fecha y tipo correctamente",
                        lista
                )
        );
    }

    /* =============================================================================
     *  FILTRAR POR FECHA, TIPO Y CIUDAD
     * =============================================================================
     */

    @PostMapping("/filtrar-fecha-tipo-ciudad")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> filtrarEventosPorFechaYTipoYCiudad(
            @RequestBody FiltrarPorFechaYTipoYCiudadDTO dto
    ) {

        List<ItemEventoDTO> lista =
                eventoServicio.filtrarEventosPorFechaYTipoYCiudad(dto);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Eventos filtrados por fecha, tipo y ciudad correctamente",
                        lista
                )
        );
    }

    /* =============================================================================
     *  FILTRAR POR TIPO Y CIUDAD
     * =============================================================================
     */

    @PostMapping("/filtrar-tipo-ciudad")
    public ResponseEntity<MensajeDTO<List<ItemEventoDTO>>> filtrarEventosPorTipoYCiudad(
            @RequestBody FiltrarPorTipoYCiudadDTO dto
    ) {

        List<ItemEventoDTO> lista =
                eventoServicio.filtrarEventosPorTipoYCiudad(dto);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Eventos filtrados por tipo y ciudad correctamente",
                        lista
                )
        );
    }
}