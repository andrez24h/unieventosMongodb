package dev.andresm.repositorios;

import dev.andresm.modelo.Cuenta;
import dev.andresm.modelo.EstadoCuenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CuentaRepo extends MongoRepository<Cuenta, String> {

    // Query("select c from Cliente c where c.correo = :email") || c.correo = ?1")
    @Query("{ 'email': ?0 }")
    Cuenta obtenerPorEmail(String email);

    Optional<Cuenta> findByEmail(String email);

    @Query("{id: ?0}")
    Optional<Cuenta> buscarId(String id);

    //@Query("select c from Cliente c where c.correo = :correo and c.password")
    @Query("{ 'email': ?0, 'password': ?1 }")
    Cuenta AutenticacionEmail(String email, String passwowd);


    //@Query("select c from Cuenta c where c.estado = :estado")
    @Query("{ 'estado': ?0 }") // True = 1 , False = 0
    Page<Cuenta> obtenerPorEstado(EstadoCuenta estado, Pageable paginador);

}
