package dev.andresm.unieventosMongodb.repositorios;

import dev.andresm.unieventosMongodb.documentos.Evento;
import dev.andresm.unieventosMongodb.documentos.TipoEvento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepo extends MongoRepository<Evento, String> {

    /**
     * 🔹 Buscar evento por ID.
     * Se utiliza _id porque MongoDB maneja el identificador
     * principal con ese nombre internamente.
     *
     * @param id Identificador del evento
     * @return Evento si existe
     */
    @Query("{ _id: ?0 }")
    Optional<Evento> buscarId(String id);

    /**
     * 🔹 Buscar evento por tipo.
     * Ejemplo de tipos:
     * CONCIERTO, TEATRO, DEPORTE, CONFERENCIA, etc.
     *
     * @param tipo Tipo del evento
     * @return Evento encontrado
     */
    @Query("{ tipo: ?0 }")
    List<Evento> buscarPorTipo(
            TipoEvento tipo);

    /**
     * 🔹 Buscar evento por ciudad exacta.
     *
     * @param ciudad Ciudad del evento
     * @return Evento encontrado
     */
    @Query("{ ciudad: ?0 }")
    List<Evento> buscarPorCiudad(
            String ciudad);

    /**
     * 🔹 Buscar evento por nombre exacto.
     *
     * @param nombre Nombre completo del evento
     * @return Evento encontrado
     */
    @Query("{ nombre: ?0 }")
    Optional<Evento> buscarPorNombre(String nombre);

    /**
     * - Verificar si existe OTRO evento con el mismo nombre,
     * excluyendo un ID específico.

     * Se utiliza principalmente en la edición de eventos para validar
     * que no exista otro evento distinto con el mismo nombre,
     * permitiendo que el evento conserve su nombre original
     * sin generar conflicto.

     * MongoDB:
     * - nombre: ?0  → nombre del evento
     * - _id: { $ne: ?1 } → diferente al ID enviado
     *
     * @param nombre Nombre del evento
     * @param id Identificador del evento que se está editando
     * @return Optional con el evento encontrado si existe conflicto
     */
    @Query("{ nombre: ?0, _id: { $ne: ?1 } }")
    Optional<Evento> buscarPorNombreIdDiferente(String nombre, String id);

    /**
     * 🔹 Buscar evento por nombre parcial (LIKE).
     * Se usa una expresión regular con opción 'i'
     * para hacer la búsqueda insensible a mayúsculas.
     *
     * @param nombre Parte del nombre del evento
     * @return Evento encontrado
     */
    @Query("{ nombre: { $regex: ?0, $options: 'i' } }")
    List<Evento> buscarPorNombreParcial(
            String nombre);

   /**
     * 🔹 Buscar evento por nombre y ciudad.
     *
     * @param nombre Nombre del evento
     * @param ciudad Ciudad donde se realiza
     * @return Evento encontrado
     */
    @Query("{ nombre: ?0, ciudad: ?1 }")
    List<Evento> buscarPorNombreYCiudad(
            String nombre,
            String ciudad);

    /**
     * 🔹 Buscar eventos dentro de un rango de fechas.
     * Usa operadores MongoDB:
     * - $gte → mayor o igual
     * - $lte → menor o igual
     *
     * @param inicio Fecha inicial
     * @param fin Fecha final
     * @return Evento encontrado
     */
    @Query("{ fecha: { $gte: ?0, $lte: ?1 } }")
    List<Evento> buscarPorFecha(
            LocalDateTime inicio,
            LocalDateTime fin);

    /**
     * 🔹 Buscar eventos por rango de fechas y ciudad.
     *
     * @param inicio Fecha inicial
     * @param fin Fecha final
     * @param ciudad Ciudad del evento
     * @return Evento encontrado
     */
    @Query("{ fecha: { $gte: ?0, $lte: ?1 }, ciudad: ?2 }")
    List<Evento> buscarPorFechaYCiudad(
            LocalDateTime inicio,
            LocalDateTime fin,
            String ciudad);

    /**
     * 🔹 Buscar eventos por rango de fechas y tipo.
     *
     * @param inicio Fecha inicial
     * @param fin Fecha final
     * @param tipo Tipo de evento
     * @return Evento encontrado
     */
    @Query("{ fecha: { $gte: ?0, $lte: ?1 }, tipo: ?2 }")
    List<Evento> buscarPorFechaYTipo(
            LocalDateTime inicio,
            LocalDateTime fin,
            TipoEvento tipo);

    /**
     * 🔹 Buscar eventos por rango de fechas, tipo y ciudad.
     * Consulta avanzada usada para filtros combinados.
     *
     * @param inicio Fecha inicial
     * @param fin Fecha final
     * @param tipo Tipo de evento
     * @param ciudad Ciudad del evento
     * @return Evento encontrado
     */
    @Query("{ fecha: { $gte: ?0, $lte: ?1 }, tipo: ?2, ciudad: ?3 }")
    List<Evento> buscarPorFechaTipoYCiudad(
            LocalDateTime inicio,
            LocalDateTime fin,
            TipoEvento tipo,
            String ciudad
    );

    /**
     * 🔹 Buscar eventos por tipo y ciudad.
     *
     * @param tipo Tipo del evento
     * @param ciudad Ciudad del evento
     * @return Evento encontrado
     */
    @Query("{ tipo: ?0, ciudad: ?1 }")
    List<Evento> buscarPorTipoYCiudad(
            TipoEvento tipo,
            String ciudad);

    /**
     * 🔹 Listar todos los eventos registrados.
     * Retorna todos los documentos de la colección evento.
     *
     * @return Lista de eventos
     */
    @Query("{}")
    List<Evento> listarTodos();



}
