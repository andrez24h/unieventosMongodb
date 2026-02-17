package dev.andresm.unieventosMongodb.repositorios;

import dev.andresm.unieventosMongodb.documentos.Cuenta;
import dev.andresm.unieventosMongodb.documentos.EstadoCuenta;
import dev.andresm.unieventosMongodb.documentos.Rol;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaRepo extends MongoRepository<Cuenta, String> {

    /**
     * - Buscar una cuenta por su identificador único.
     * Se utiliza el campo {@code _id} ya que MongoDB maneja internamente
     * los identificadores primarios con ese nombre.
     *
     * @param id identificador único de la cuenta
     * @return un {@link Optional} con la cuenta si existe
     */
    @Query("{ _id: ?0 }")
    Optional<Cuenta> buscarId(String id);

    /**
     * - Buscar una cuenta por correo electrónico.
     * Este método se usa principalmente para:
     * - Validaciones de registro
     * - Inicio de sesión
     * - Recuperación de contraseña
     *
     * @param email correo electrónico de la cuenta
     * @return un {@link Optional} con la cuenta si existe
     */
    // Query("select c from Cliente c where c.correo = :email") || c.correo = ?1")
    @Query("{ email: ?0 }")
    Optional<Cuenta> buscarEmail(String email);

    /**
     * - Buscar una cuenta por la cédula del usuario asociado.
     * La cédula se encuentra embebida dentro del objeto {@code usuario}
     * de la cuenta.
     * Se utiliza para búsquedas administrativas o validaciones
     * basadas en información personal del usuario.
     *
     * @param cedula cédula del usuario
     * @return un {@link Optional} con la cuenta si existe
     */
    @Query("{ 'usuario.cedula': ?0 }")
    Optional<Cuenta> buscarCedula(String cedula);

    /**
     * - Buscar una cuenta por email excluyendo un ID específico.

     * Este método se utiliza principalmente en procesos de actualización,
     * para validar que no exista otra cuenta con el mismo email
     * diferente a la que se está editando.

     * La condición {_id: { $ne: ?1 }} indica:
     * "Buscar documentos cuyo ID sea distinto al proporcionado".
     *
     * @param email correo electrónico a validar
     * @param id identificador de la cuenta que se está actualizando
     * @return un {@link Optional} con la cuenta encontrada si existe otra con el mismo email
     */
    @Query("{ email: ?0, _id: { $ne: ?1 } }")
    Optional<Cuenta> buscarEmailIdDiferente(String email, String id);

    /**
     * - Buscar una cuenta por cédula excluyendo un ID específico.
     *
     * Similar a {@code buscarEmailIdDiferente}, pero aplicado a la cédula
     * del usuario embebido dentro de la cuenta.
     *
     * Se usa para validar que no exista otra cuenta con la misma cédula
     * diferente a la que se está editando.
     *
     * @param cedula cédula a validar
     * @param id identificador de la cuenta que se está actualizando
     * @return un {@link Optional} con la cuenta encontrada si existe otra con la misma cédula
     */
    @Query("{ 'usuario.cedula': ?0, _id: { $ne: ?1 } }")
    Optional<Cuenta> buscarCedulaIdDiferente(String cedula, String id);

    /**
     * - Autenticación de una cuenta mediante correo y contraseña.
     * Retorna la cuenta únicamente si las credenciales coinciden.
     *  Nota: la validación de contraseñas debería manejarse
     * preferiblemente con mecanismos de cifrado.
     *
     * @param email correo electrónico
     * @param password contraseña de la cuenta
     * @return un {@link Optional} con la cuenta autenticada
     */
    //@Query("select c from Cliente c where c.correo = :correo and c.password")
    @Query("{ email: ?0, password: ?1 }")
    Optional<Cuenta> autenticacionEmail(String email, String password);


    /**
     * - Obtener cuentas filtradas por estado.
     * Permite consultar cuentas activas, inactivas o eliminadas,
     * incluyendo soporte para paginación.
     *
     * @param estado estado de la cuenta (ACTIVO, INACTIVO, ELIMINADO)
     * @param paginador configuración de paginación
     * @return una página de cuentas que coinciden con el estado
     */
    //@Query("select c from Cuenta c where c.estado = :estado")
    @Query("{ estado: ?0 }") // True = 1 , False = 0
    Page<Cuenta> obtenerPorEstado(EstadoCuenta estado, Pageable paginador);

    /**
     * - Buscar cuentas por rol.
     * Ejemplos de rol:
     * - CLIENTE
     * - ADMIN
     * - ORGANIZADOR
     * @param rol rol de la cuenta
     * @return lista de cuentas que tienen el rol indicado
     */
    @Query("{ rol: ?0 }")
    List<Cuenta> buscarPorRol(Rol rol);
}
