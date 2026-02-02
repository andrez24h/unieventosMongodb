package dev.andresm.unieventosMongodb.repositorios;

import dev.andresm.unieventosMongodb.documentos.Cuenta;
import dev.andresm.unieventosMongodb.documentos.Evento;
import dev.andresm.unieventosMongodb.documentos.TipoEvento;
import jakarta.validation.constraints.NotNull;
import org.bson.types.ObjectId;
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
    Optional<Evento> buscarPorTipo(TipoEvento tipo);

    /**
     * 🔹 Buscar evento por ciudad exacta.
     *
     * @param ciudad Ciudad del evento
     * @return Evento encontrado
     */
    @Query("{ ciudad: ?0 }")
    Optional<Evento> buscarPorCiudad(String ciudad);

    /**
     * 🔹 Buscar evento por nombre exacto.
     *
     * @param nombre Nombre completo del evento
     * @return Evento encontrado
     */
    @Query("{ nombre: ?0 }")
    Optional<Evento> buscarPorNombre(String nombre);

    /**
     * 🔹 Buscar evento por nombre parcial (LIKE).
     * Se usa una expresión regular con opción 'i'
     * para hacer la búsqueda insensible a mayúsculas.
     *
     * @param nombre Parte del nombre del evento
     * @return Evento encontrado
     */
    @Query("{ nombre: { $regex: ?0, $options: 'i' } }")
    Optional<Evento> buscarPorNombreParcial(String nombre);

    /**
     * 🔹 Buscar evento por dirección parcial.
     * Permite búsquedas flexibles por dirección,
     * útil para filtros en frontend.
     *
     * @param direccion Texto parcial de la dirección
     * @return Evento encontrado
     */
    @Query("{ direccion: { $regex: ?0, $options: 'i' } }")
    Optional<Evento> buscarPorDireccionParcial(String direccion);

    /**
     * 🔹 Buscar evento por nombre y ciudad.
     *
     * @param nombre Nombre del evento
     * @param ciudad Ciudad donde se realiza
     * @return Evento encontrado
     */
    @Query("{ nombre: ?0, ciudad: ?1 }")
    Optional<Evento> buscarPorNombreYCiudad(String nombre, String ciudad);

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
    Optional<Evento> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);

    /**
     * 🔹 Buscar eventos por rango de fechas y ciudad.
     *
     * @param inicio Fecha inicial
     * @param fin Fecha final
     * @param ciudad Ciudad del evento
     * @return Evento encontrado
     */
    @Query("{ fecha: { $gte: ?0, $lte: ?1 }, ciudad: ?2 }")
    Optional<Evento> buscarPorFechasYCiudad(LocalDateTime inicio, LocalDateTime fin, String ciudad);

    /**
     * 🔹 Buscar eventos por rango de fechas y tipo.
     *
     * @param inicio Fecha inicial
     * @param fin Fecha final
     * @param tipo Tipo de evento
     * @return Evento encontrado
     */
    @Query("{ fecha: { $gte: ?0, $lte: ?1 }, tipo: ?2 }")
    Optional<Evento> buscarPorFechasYTipo(LocalDateTime inicio, LocalDateTime fin, TipoEvento tipo);

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
    Optional<Evento> buscarPorFechasTipoYCiudad(
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
    Optional<Evento> buscarPorTipoYCiudad(TipoEvento tipo, String ciudad);

    /**
     * 🔹 Listar todos los eventos registrados.
     * Retorna todos los documentos de la colección evento.
     *
     * @return Lista de eventos
     */
    @Query("{}")
    List<Evento> listarTodos();



}
