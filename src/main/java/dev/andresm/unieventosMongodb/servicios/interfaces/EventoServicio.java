package dev.andresm.unieventosMongodb.servicios.interfaces;

import dev.andresm.unieventosMongodb.documentos.Evento;
import dev.andresm.unieventosMongodb.documentos.TipoEvento;
import dev.andresm.unieventosMongodb.dto.evento.*;

import java.util.List;

/**
 * - Servicio de gestión de eventos.
 * - Define las operaciones relacionadas con:
 * - Creación, edición y eliminación de eventos
 * - Consulta de información detallada de eventos
 * - Verificación de disponibilidad de localidades
 * - Listado y filtrado de eventos por distintos criterios
 */
public interface EventoServicio {

    /**
     * - Crear un nuevo evento en el sistema.
     *
     * @param crearEventoDTO datos necesarios para crear el evento
     * @return identificador del evento creado
     * @throws Exception si el nombre del evento ya existe o la cuenta no existe
     */
    String crearEvento(CrearEventoDTO crearEventoDTO) throws Exception;

    /**
     * - Editar la información de un evento existente.
     *
     * @param editarEventoDTO datos actualizados del evento
     * @return identificador del evento editado
     * @throws Exception si el evento no existe o el nombre ya está en uso
     */
    String editarEvento(EditarEventoDTO editarEventoDTO) throws Exception;

    /**
     * - Eliminar un evento del sistema.
     *
     * @param id identificador del evento
     * @return identificador del evento eliminado
     * @throws Exception si el evento no existe
     */
    String eliminarEvento(String id) throws Exception;

    /**
     * - Obtener la información completa de un evento específico.
     *
     * @param id identificador del evento
     * @return información detallada del evento
     * @throws Exception si el evento no existe
     */
    InformacionEventoDTO obtenerInformacionEvento(String id) throws Exception;

    /**
     * - Listar todos los eventos registrados en el sistema.
     *
     * @return lista resumida de eventos
     */
    List<ItemEventoDTO> listarEventos();

    /**
     * - Verificar la disponibilidad de una localidad dentro de un evento.
     *
     * @param disponiblidadEventoDTO datos necesarios para la verificación
     * @return true si hay disponibilidad suficiente
     * @throws Exception si el evento no existe
     */
    boolean disponiblidad(DisponibilidadEventoDTO disponiblidadEventoDTO) throws Exception;

    /**
     * - Obtener un evento completo a partir de su identificador.
     *
     * @param id identificador del evento
     * @return evento encontrado
     * @throws Exception si el evento no existe
     */
    Evento obtenerEvento(String id) throws Exception;

    /* ======================= FILTROS ===================================================== */

    /**
     * - Filtrar eventos de forma dinámica utilizando múltiples criterios opcionales.
     * <p>
     * Este método permite aplicar filtros combinados tales como:
     * - Nombre (búsqueda parcial)
     * - Ciudad
     * - Tipo de evento
     * - Fecha o rango de fechas
     * <p>
     * Si algún campo del DTO es nulo, dicho criterio no será tenido en cuenta
     * en la consulta.
     * <p>
     * Este método es utilizado principalmente en el frontend
     * para el componente de búsqueda avanzada de eventos.
     *
     * @param filtroEventoDTO datos opcionales para aplicar el filtrado
     * @return lista de eventos que cumplen los criterios indicados
     */
    List<ItemEventoDTO> filtrarEventos(FiltroEventoDTO filtroEventoDTO);

    /**
     * - Listar eventos filtrados por ciudad.
     *
     * @param ciudad ciudad donde se realiza el evento
     * @return lista de eventos encontrados
     */
    List<ItemEventoDTO> filtrarEventosPorCiudad(String ciudad) throws Exception;

    /**
     * - Listar eventos filtrados por tipo.
     *
     * @param tipo tipo de evento
     * @return lista de eventos encontrados
     */
    List<ItemEventoDTO> filtrarEventosPorTipo(TipoEvento tipo);

    /**
     * - Listar eventos filtrados por nombre (búsqueda parcial).
     *
     * @param nombre nombre o parte del nombre del evento
     * @return lista de eventos encontrados
     */
    List<ItemEventoDTO> filtrarEventosPorNombre(String nombre);

    /**
     * - Listar eventos filtrados por nombre y ciudad.
     *
     * @param filtrarPorNombreYCiudadDTO datos del filtro
     * @return lista de eventos encontrados
     */
    List<ItemEventoDTO> filtrarEventosPorNombreYCiudad(FiltrarPorNombreYCiudadDTO filtrarPorNombreYCiudadDTO);

    /**
     * - Listar eventos dentro de un rango de fechas.
     *
     * @param filtrarPorFechaDTO datos del rango de fechas
     * @return lista de eventos encontrados
     */
    List<ItemEventoDTO> filtrarEventosPorFecha(FiltrarPorFechaDTO filtrarPorFechaDTO);

    /**
     * - Listar eventos por rango de fechas y ciudad.
     *
     * @param filtrarEventosPorFechaYCiudad datos del filtro
     * @return lista de eventos encontrados
     */
    List<ItemEventoDTO> filtrarEventosPorFechaYCiudad(FiltrarPorFechaYCiudadDTO filtrarEventosPorFechaYCiudad);

    /**
     * - Listar eventos dentro de un rango de fechas y por tipo específico.
     * <p>
     * Permite consultar eventos que:
     * - Estén dentro del rango de fechas indicado
     * - Correspondan al tipo de evento seleccionado
     * <p>
     * Este filtro es útil cuando el usuario desea consultar, por ejemplo:
     * conciertos dentro de una semana específica.
     *
     * @param filtrarPorFechaYTipoDTO datos del rango de fechas y tipo
     * @return lista de eventos encontrados
     */
    List<ItemEventoDTO> filtrarEventosPorFechaYTipo(FiltrarPorFechaYTipoDTO filtrarPorFechaYTipoDTO);

    /**
     * - Listar eventos por rango de fechas, tipo y ciudad.
     *
     * @param filtrarPorFechaYTipoYCiudadDTO datos del filtro
     * @return lista de eventos encontrados
     */
    List<ItemEventoDTO> filtrarEventosPorFechaYTipoYCiudad(FiltrarPorFechaYTipoYCiudadDTO filtrarPorFechaYTipoYCiudadDTO);

    /**
     * - Listar eventos filtrados por tipo y ciudad.
     *
     * @param filtrarEventosPorTipoYCiudad datos del filtro
     * @return lista de eventos encontrados
     */
    List<ItemEventoDTO> filtrarEventosPorTipoYCiudad(FiltrarPorTipoYCiudadDTO filtrarEventosPorTipoYCiudad);
}