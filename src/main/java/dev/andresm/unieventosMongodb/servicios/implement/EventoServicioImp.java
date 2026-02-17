package dev.andresm.unieventosMongodb.servicios.implement;

import dev.andresm.unieventosMongodb.documentos.Cuenta;
import dev.andresm.unieventosMongodb.documentos.Evento;
import dev.andresm.unieventosMongodb.documentos.TipoEvento;
import dev.andresm.unieventosMongodb.dto.evento.*;
import dev.andresm.unieventosMongodb.repositorios.CuentaRepo;
import dev.andresm.unieventosMongodb.repositorios.EventoRepo;
import dev.andresm.unieventosMongodb.servicios.interfaces.EventoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *  Implementación del servicio de eventos.

 * Esta clase contiene la lógica de negocio relacionada con:
 * - Creación de eventos
 * - Edición de eventos
 * - Eliminación de eventos
 * - Consulta de información
 * - Verificación de disponibilidad
 * - Filtros de búsqueda

 * Todas las operaciones se realizan sobre MongoDB
 * utilizando repositorios explícitos (sin métodos inferidos).
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EventoServicioImp implements EventoServicio {

    private final CuentaRepo cuentaRepo;
    private final EventoRepo eventoRepo;

    /**
     * - Crea un nuevo evento en el sistema.
     * <p>
     * Valida que:
     * - El nombre del evento no esté previamente registrado
     * - La cuenta asociada al evento exista
     * <p>
     * Si todas las validaciones se cumplen:
     * - Se construye el evento usando Builder
     * - Se guarda en la base de datos
     *
     * @param crearEventoDTO datos necesarios para crear el evento
     * @return ID del evento creado
     * @throws Exception si el nombre ya existe o la cuenta no existe
     */
    @Override
    public String crearEvento(CrearEventoDTO crearEventoDTO) throws Exception {

        if (existeNombre(crearEventoDTO.nombre())) {
            throw new Exception("El nombre yá existe");
        }

        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarId(crearEventoDTO.idUsuario());

        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta no existe");
        }

        Evento evento = Evento.builder()
                .idUsuario(crearEventoDTO.idUsuario())
                .ciudad(crearEventoDTO.ciudad())
                .descripcion(crearEventoDTO.descripcion())
                .direccion(crearEventoDTO.direccion())
                .estado(crearEventoDTO.estado())
                .fecha(crearEventoDTO.fecha())
                .imagenPortada(crearEventoDTO.imagenPortada())
                .imagenLocalidades(crearEventoDTO.imagenLocalidades())
                .localidades(crearEventoDTO.localidades())
                .nombre(crearEventoDTO.nombre())
                .ubicacion(crearEventoDTO.ubicacion())
                .tipo(crearEventoDTO.tipo())
                .build();

        eventoRepo.save(evento);
        return evento.getId();
    }

    /**
     * - Edita un evento existente.

     * Valida que:
     * - El evento exista en el sistema
     * - El nombre no esté siendo usado por otro evento diferente

     * @param editarEventoDTO datos actualizados del evento
     * @return ID del evento editado
     * @throws Exception si el evento no existe o el nombre ya está en uso
     */
    @Override
    public String editarEvento(EditarEventoDTO editarEventoDTO) throws Exception {

        // 1. Verificar que el evento exista
        Optional<Evento> eventoOptional = eventoRepo.buscarId(editarEventoDTO.id());

        if (eventoOptional.isEmpty()) {
            throw new Exception("No existe el evento");
        }

        Evento evento = eventoOptional.get();

        // 2. Validar que no exista OTRO evento con el mismo nombre
        Optional<Evento> eventoConMismoNombre = eventoRepo.buscarPorNombreIdDiferente(editarEventoDTO.nombre(),editarEventoDTO.id());

        if (eventoConMismoNombre.isPresent()) {
            throw new Exception("El nombre ya está en uso");
        }

        // 3. Actualizar datos
        evento.setNombre(editarEventoDTO.nombre());
        evento.setCiudad(editarEventoDTO.ciudad());
        evento.setDescripcion(editarEventoDTO.descripcion());
        evento.setDireccion(editarEventoDTO.direccion());
        evento.setEstado(editarEventoDTO.estado());
        evento.setFecha(editarEventoDTO.fecha());
        evento.setImagenPortada(editarEventoDTO.imagenPortada());
        evento.setImagenLocalidades(editarEventoDTO.imagenLocalidades());
        evento.setLocalidades(editarEventoDTO.localidades());
        evento.setUbicacion(editarEventoDTO.ubicacion());
        evento.setTipo(editarEventoDTO.tipo());

        // 4. Guardar cambios
        eventoRepo.save(evento);
        return evento.getId();
    }

    /**
     * Elimina un evento por ID.
     */
    @Override
    public String eliminarEvento(String id) throws Exception {

        Optional<Evento> eventoOptional = eventoRepo.buscarId(id);

        if (eventoOptional.isEmpty()) {
            throw new Exception("No existe el evento");
        }

        eventoRepo.delete(eventoOptional.get());
        return id;
    }

    /**
     * - Obtiene la información completa de un evento.
     */
    @Override
    public InformacionEventoDTO obtenerInformacionEvento(String id) throws Exception {

        Optional<Evento> eventoOptional = eventoRepo.buscarId(id);

        if (eventoOptional.isEmpty()) {
            throw new Exception("No existe el evento");
        }

        Evento evento = eventoOptional.get();

        return new InformacionEventoDTO(
                evento.getId(),
                evento.getNombre(),
                evento.getDireccion(),
                evento.getCiudad(),
                evento.getDescripcion(),
                evento.getImagenPortada(),
                evento.getImagenLocalidades(),
                evento.getTipo(),
                evento.getEstado(),
                evento.getUbicacion(),
                evento.getFecha(),
                evento.getLocalidades()
        );
    }

    /**
     * - Lista todos los eventos.
     */
    @Override
    public List<ItemEventoDTO> listarEventos() {

        return eventoRepo.listarTodos().stream().map(this::mapItemEvento).collect(Collectors.toList());
    }

    /* =======================   DISPONIBILIDAD   =========================================== */
    /**
     * - Verifica disponibilidad de localidades.
     */
    @Override
    public boolean disponiblidad(DisponibilidadEventoDTO disponiblidadEventoDTO) throws Exception {

       Optional<Evento> eventoOptional = eventoRepo.buscarId(disponiblidadEventoDTO.idEvento());;

       if (eventoOptional.isEmpty()) {
           throw new Exception("No existe el evento");
       }
        Evento evento = eventoOptional.get();

       return evento.getLocalidades().stream().anyMatch(localidad -> localidad.getNombre().equals(disponiblidadEventoDTO.nombreLocalidad()) && localidad.cantidadDisponible() >= disponiblidadEventoDTO.cantidad());
    }

    /**
     * - Obtiene un evento por su ID.

     * @param id ID del evento
     * @return Evento encontrado
     * @throws Exception si el evento no existe
     */
    @Override
    public Evento obtenerEvento(String id) throws Exception {

        Optional<Evento> eventoOptional = eventoRepo.buscarId(id);

        if (eventoOptional.isEmpty()) {
            throw new Exception("No existe el evento");
        }
        return eventoOptional.get();
    }

    /* ========================   FILTROS   =================================================== */

    /**
     * Filtra eventos según los criterios enviados en el DTO.

     * Los criterios son opcionales:
     * - Nombre (búsqueda parcial)
     * - Ciudad
     * - Tipo de evento
     * - Fecha específica (día)

     * @param filtroEventoDTO criterios de filtrado
     * @return lista de eventos que cumplen los criterios
     */
    @Override
    public List<ItemEventoDTO> filtrarEventos(FiltroEventoDTO filtroEventoDTO) {

        return eventoRepo.listarTodos().stream()
                .filter(evento ->
                        (filtroEventoDTO.nombre() == null || evento.getNombre().toLowerCase().contains(filtroEventoDTO.nombre().toLowerCase())) &&
                                (filtroEventoDTO.ciudad() == null || evento.getCiudad().equalsIgnoreCase(filtroEventoDTO.ciudad())) &&
                                (filtroEventoDTO.tipo() == null || evento.getTipo().equals(filtroEventoDTO.tipo())) &&
                                (filtroEventoDTO.fecha() == null || evento.getFecha().toLocalDate().isEqual(filtroEventoDTO.fecha()))
                )
                .map(this::mapItemEvento)
                .toList();
    }

    /**
     * Filtra los eventos por ciudad.
     *
     * @param ciudad ciudad del evento
     * @return lista de eventos realizados en la ciudad indicada
     */
    @Override
    public List<ItemEventoDTO> filtrarEventosPorCiudad(String ciudad) {

        return eventoRepo.buscarPorCiudad(ciudad).stream()
                .map(this::mapItemEvento)
                .toList();
    }

    /**
     * Filtra los eventos por tipo.
     *
     * @param tipo de evento
     * @return lista de eventos del tipo indicado
     */
    @Override
    public List<ItemEventoDTO> filtrarEventosPorTipo(TipoEvento tipo) {

        return eventoRepo.buscarPorTipo(tipo)
                .stream()
                .map(this::mapItemEvento)
                .toList();
    }

    /**
     * Filtra eventos cuyo nombre contenga el texto indicado.
     *
     * @param nombre texto a buscar en el nombre del evento
     * @return lista de eventos coincidentes
     */
    @Override
    public List<ItemEventoDTO> filtrarEventosPorNombre(String nombre) {

        return eventoRepo.buscarPorNombreParcial(nombre)
                .stream()
                .map(this::mapItemEvento)
                .toList();
        }

    /**
     * Filtra eventos por nombre y ciudad.
     *
     * @param  filtrarPorNombreYCiudadDTO con nombre y ciudad
     * @return lista de eventos encontrados
     */
    @Override
    public List<ItemEventoDTO> filtrarEventosPorNombreYCiudad(FiltrarPorNombreYCiudadDTO filtrarPorNombreYCiudadDTO) {

        return eventoRepo.buscarPorNombreYCiudad(filtrarPorNombreYCiudadDTO.nombre(), filtrarPorNombreYCiudadDTO.ciudad())
                .stream()
                .map(this::mapItemEvento)
                .toList();
    }

    /**
     * Filtra eventos por una fecha específica (día).
     *
     * @param filtrarPorFechaDTO con la fecha a buscar
     * @return lista de eventos en esa fecha
     */
    @Override
    public List<ItemEventoDTO> filtrarEventosPorFecha(FiltrarPorFechaDTO filtrarPorFechaDTO) {

        LocalDate fecha = filtrarPorFechaDTO.fecha();

        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(23, 59, 59);

        return eventoRepo.buscarPorFecha(inicio, fin)
                .stream()
                .map(this::mapItemEvento)
                .toList();
    }

    /**
     * Filtra eventos por fecha y ciudad.
     *
     * @param filtrarEventosPorFechaYCiudadDTO con fecha y ciudad
     * @return lista de eventos encontrados
     */
    @Override
    public List<ItemEventoDTO> filtrarEventosPorFechaYCiudad(FiltrarPorFechaYCiudadDTO filtrarEventosPorFechaYCiudadDTO) {

        LocalDateTime inicio = filtrarEventosPorFechaYCiudadDTO.fecha().atStartOfDay();
        LocalDateTime fin = filtrarEventosPorFechaYCiudadDTO.fecha().atTime(23, 59, 59);

        return eventoRepo.buscarPorFechaYCiudad(inicio, fin, filtrarEventosPorFechaYCiudadDTO.ciudad())
                .stream()
                .map(this::mapItemEvento)
                .toList();
    }

    @Override
    public List<ItemEventoDTO> filtrarEventosPorFechaYTipo(FiltrarPorFechaYTipoDTO filtrarPorFechaYTipoDTO) {

        LocalDateTime inicio = filtrarPorFechaYTipoDTO.fecha().atStartOfDay();
        LocalDateTime fin = filtrarPorFechaYTipoDTO.fecha().atTime(23, 59, 59);

        return eventoRepo.buscarPorFechaYTipo(inicio, fin, filtrarPorFechaYTipoDTO.tipo())
                .stream()
                .map(this::mapItemEvento)
                .toList();
    }

    /**
     * Filtra eventos por rango de fechas, tipo y ciudad.
     *
     * @param filtrarPorFechaYTipoYCiudadDTO criterios de filtrado
     * @return lista de eventos encontrados
     */

    @Override
    public List<ItemEventoDTO> filtrarEventosPorFechaYTipoYCiudad(FiltrarPorFechaYTipoYCiudadDTO filtrarPorFechaYTipoYCiudadDTO) {

        LocalDateTime inicio = filtrarPorFechaYTipoYCiudadDTO.fecha().atStartOfDay();
        LocalDateTime fin = filtrarPorFechaYTipoYCiudadDTO.fecha().atTime(23, 59, 59);

        return eventoRepo.buscarPorFechaTipoYCiudad(inicio, fin, filtrarPorFechaYTipoYCiudadDTO.tipo(), filtrarPorFechaYTipoYCiudadDTO.ciudad())
                .stream()
                .map(this::mapItemEvento)
                .toList();
    }

    @Override
    public List<ItemEventoDTO> filtrarEventosPorTipoYCiudad(FiltrarPorTipoYCiudadDTO filtrarEventosPorTipoYCiudad) {

        return eventoRepo.buscarPorTipoYCiudad(filtrarEventosPorTipoYCiudad.tipo(),
                filtrarEventosPorTipoYCiudad.ciudad())
                .stream()
                .map(this::mapItemEvento)
                .toList();
    }

    /* =====================   MÉTODOS AUXILIARES   ========================================= */

    /**
     * Verifica si existe un evento con el ID dado.
     */
    private boolean existeId(String id) {
        return eventoRepo.buscarId(id).isPresent();
    }

    /**
     * Verifica si existe un evento con el nombre dado.
     */
    private boolean existeNombre(String nombre) {
        return eventoRepo.buscarPorNombre(nombre).isPresent();
    }

    /**
     * Mapea un evento a su DTO resumido.
     */
    private ItemEventoDTO mapItemEvento(Evento evento) {
        return new ItemEventoDTO(
                evento.getId(),
                evento.getNombre(),
                evento.getDescripcion(),
                evento.getImagenPortada(),
                evento.getFecha(),
                evento.getDireccion()
        );
    }
}