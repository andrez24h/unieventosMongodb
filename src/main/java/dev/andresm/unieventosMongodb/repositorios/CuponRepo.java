package dev.andresm.unieventosMongodb.repositorios;

import dev.andresm.unieventosMongodb.documentos.Cupon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 🔹 Repositorio para la gestión de cupones.
 * Proporciona métodos de acceso a datos para la colección
 * de cupones en MongoDB.
 */
@Repository
public interface CuponRepo extends MongoRepository<Cupon, String> {

    /**
     * 🔹 Buscar un cupón por su identificador.
     *
     * @param id identificador del cupón
     * @return Optional con el cupón si existe
     */
    @Query("{ _id:  ?0 }")
    Optional<Cupon> buscarId(String id);

    /**
     * 🔹 Buscar un cupón por su código.
     *
     * @param codigo código único del cupón
     * @return Optional con el cupón si existe
     */
    @Query("{ codigo: ?0 }")
    Optional<Cupon> buscarCodigo(String codigo);

    /**
     * 🔹 Buscar un cupón por su nombre.
     *
     * @param nombre nombre del cupón
     * @return Optional con el cupón si existe
     */
    @Query("{ nombre: ?0 }")
    Optional<Cupon> buscarNombre(String nombre);

    /**
     * 🔹 Buscar cupones asociados a un beneficiario.
     *
     * @param beneficiarios identificador del beneficiario
     * @return lista de cupones asociados
     */
    @Query("{ beneficiarios: ?0 }")
    List<Cupon> buscarListaBeneficiarios(String beneficiarios);
}
