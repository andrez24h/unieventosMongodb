package dev.andresm.unieventosMongodb.repositorios;

import dev.andresm.unieventosMongodb.documentos.Orden;
import dev.andresm.unieventosMongodb.dto.orden.ItemOrdenDTO;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio encargado de la gestión y consulta de órdenes en MongoDB.
 * Extiende MongoRepository para proporcionar operaciones CRUD básicas.
 */
@Repository
public interface OrdenRepo extends MongoRepository<Orden, String> {

    /**
     * - Buscar una orden por su ID.
     * Se usa _id porque MongoDB maneja el identificador con ese nombre.
     */
    @Query("{ _id: ?0 }")
    Optional<Orden> buscarId(String id);

    /**
     * Obtiene todas las órdenes asociadas a un cliente específico.
     *
     * @param idCliente ID del cliente
     * @return Lista completa de órdenes
     */
    @Query("{ idCliente: ?0 }")
    List<Orden> buscarOrdenesPorCliente(String idCliente);

    /**
     * - Buscar una orden por el código devuelto por la pasarela de pago.
     */
    @Query("{ codigoPasarela: ?0 }")
    Optional<Orden> buscarPorCodigoPasarela(String codigoPasarela);

    /**
     * 🔹 Obtener todas las órdenes asociadas a un cupón.
     */
    @Query("{ idCupon: ?0 }")
    List<Orden> buscarPorCupon(String idCupon);

    /**
     * - Listar órdenes realizadas dentro de un rango de fechas.
     */
    @Query("{ fecha: { $gte: ?0, $lte: ?1 } }")
    List<Orden> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Lista las órdenes de un usuario utilizando Aggregation en MongoDB.

     * ============================
     *  FLUJO DE EJECUCIÓN
     * ============================

     * 1. Se filtran las órdenes por idCliente.
     * 2. Se realiza un $lookup con la colección "cuentas".
     *    - Se usa pipeline porque existe conversión de tipos.
     *    - Se convierte el idCliente (String) a ObjectId para poder compararlo con _id.
     * 3. Se desestructura el resultado del lookup con $unwind.
     * 4. Se proyectan únicamente los campos necesarios para el DTO.

     * ============================
     * ⚠️ IMPORTANTE (TIPOS DE DATOS)
     * ============================
     * - Orden.idCliente es String
     * - Cuenta._id es ObjectId
     * - Por eso se usa:
     *   $toObjectId para evitar mismatch de tipos

     * ============================
     * @param idUsuario Identificador del cliente (String)
     * @return Lista de órdenes resumidas (ItemOrdenDTO)
     */
    @Aggregation({
            // 1. Filtra órdenes por cliente
            "{ $match: { idCliente: ?0 } }",

            // 2. Relaciona con colección cuentas (con conversión de tipo)
            "{ $lookup: { " +
                    "from: 'cuentas', " +
                    "let: { clienteId: '$idCliente' }, " +
                    "pipeline: [" +
                    "{ $match: { " +
                    "$expr: { " +
                    "$eq: [ '$_id', { $toObjectId: '$$clienteId' } ] " +
                    "} } }" +
                    "], " +
                    "as: 'cuenta' " +
                    "} }",

            // 3. Desestructura la cuenta (de array a objeto)
            "{ $unwind: '$cuenta' }",

            // 4. Proyección de campos finales del DTO
            "{ $project: { " +
                    "idOrden: '$_id', " +
                    "fecha: 1, " +
                    "total: 1, " +
                    "estado: 1, " +
                    "email: '$cuenta.email', " +
                    "rol: '$cuenta.rol', " +
                    "estadoCuenta: '$cuenta.estado' " +
                    "} }"
    })
    List<ItemOrdenDTO> listarOrdenesPorUsuario(String idUsuario);

    /**
     * Lista las órdenes que contienen un evento específico
     * dentro de sus ítems utilizando Aggregation.

     * IMPORTANTE (TIPO DE DATOS):
     * - idCliente en la orden es String
     * - _id en la colección cuentas es ObjectId

     * Por eso es necesario convertir el idCliente a ObjectId
     * dentro del $lookup usando $toObjectId.

     * Flujo:
     * 1. Filtra órdenes que contengan el evento en items.idEvento.
     * 2. Realiza lookup con la colección "cuentas" usando pipeline
     *    para manejar la conversión de tipos (String → ObjectId).
     * 3. Une la cuenta con la orden.
     * 4. Proyecta únicamente los campos necesarios para el DTO.
     *
     * @param idEvento Identificador del evento
     * @return Lista de órdenes resumidas (ItemOrdenDTO)
     */
    @Aggregation({
            "{ $match: { 'items.idEvento': ?0 } }",

            "{ $lookup: { " +
                    "from: 'cuentas', " +
                    "let: { clienteId: '$idCliente' }, " +
                    "pipeline: [" +
                    "{ $match: { " +
                    "$expr: { " +
                    "$eq: [ '$_id', { $toObjectId: '$$clienteId' } ] " +
                    "} " +
                    "} }" +
                    "], " +
                    "as: 'cuenta' " +
                    "} }",

            "{ $unwind: '$cuenta' }",

            "{ $project: { " +
                    "idOrden: '$_id', " +
                    "fecha: 1, " +
                    "total: 1, " +
                    "estado: 1, " +
                    "email: '$cuenta.email', " +
                    "rol: '$cuenta.rol', " +
                    "estadoCuenta: '$cuenta.estado' " +
                    "} }"
    })
    List<ItemOrdenDTO> listarOrdenesPorEvento(String idEvento);

    /**
     * Obtiene todas las órdenes que contienen un evento específico.

     * Este método retorna las órdenes completas (no DTO),
     * permitiendo acceder a los ítems, cantidades y precios
     * para cálculos internos como reportes.
     *
     * @param idEvento identificador del evento
     * @return Lista de órdenes completas
     */
    @Query("{ 'items.idEvento': ?0 }")
    List<Orden> buscarOrdenesPorEvento(String idEvento);
}
/*
================================================================================
📌 EXPLICACIÓN DEL @Aggregation (PASO A PASO)
================================================================================

1️⃣ { $match: { idCliente: ?0 } }

   - Filtra las órdenes.
   - Solo se seleccionan las órdenes cuyo idCliente
     coincide con el parámetro recibido en el método.
   - Es equivalente a:
       SELECT * FROM ordenes WHERE idCliente = ?

--------------------------------------------------------------------------------

2️⃣ { $lookup: { from: 'cuentas', localField: 'idCliente',
                 foreignField: '_id', as: 'cuenta' } }

   - Realiza una "unión" (JOIN) entre colecciones.
   - Busca en la colección "cuentas" el documento
     cuyo _id sea igual a idCliente.
   - El resultado se guarda en un arreglo llamado "cuenta".

--------------------------------------------------------------------------------

3️⃣ { $unwind: '$cuenta' }

   - Convierte el arreglo "cuenta" en un objeto.
   - Como cada orden pertenece a una sola cuenta,
     se elimina el formato de lista para facilitar el acceso.

--------------------------------------------------------------------------------

4️⃣ { $project: { ... } }

   - Define exactamente qué campos se devuelven.
   - Optimiza la consulta evitando enviar datos innecesarios.
   - Se devuelven:
       ✔ fecha de la orden
       ✔ total de la orden
       ✔ estado del pago
       ✔ nombre del usuario
       ✔ correo del usuario

   - Los nombres proyectados deben coincidir con los
     atributos definidos en ItemOrdenDTO.

================================================================================
📌 ¿POR QUÉ USAR ESTO?
================================================================================

✔ Evita múltiples consultas a la base de datos
✔ Reduce datos enviados al frontend
✔ Mantiene separadas las entidades del modelo de vista
✔ Mejora rendimiento y escalabilidad
✔ Patrón profesional usado en producción

================================================================================
*/

