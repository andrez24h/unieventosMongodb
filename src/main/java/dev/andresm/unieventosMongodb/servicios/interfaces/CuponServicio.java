package dev.andresm.unieventosMongodb.servicios.interfaces;

import dev.andresm.unieventosMongodb.dto.cupon.*;

import java.util.List;

/**
 * 🔹 Servicio de gestión de cupones.
 * Define las operaciones relacionadas con:
 * - Creación y actualización de cupones
 * - Eliminación de cupones
 * - Redención por parte de clientes
 * - Consulta de cupones disponibles
 */
public interface CuponServicio {

    /**
     * 🔹 Crear un nuevo cupón en el sistema.
     *
     * @param cuponDTO datos necesarios para crear el cupón
     * @return mensaje de confirmación
     * @throws Exception si el cupón ya existe o los datos son inválidos
     */
    String crearCupon(CrearCuponDTO cuponDTO) throws Exception;

    /**
     * 🔹 Actualizar la información de un cupón existente.
     *
     * @param cuponDTO datos actualizados del cupón
     * @return mensaje de confirmación
     * @throws Exception si el cupón no existe o no puede modificarse
     */
    String actualizarCupon(ActualizarCuponDTO cuponDTO) throws Exception;

    /**
     * 🔹 Eliminar un cupón del sistema.
     *
     * @param idCupon identificador del cupón
     * @throws Exception si el cupón no existe
     */
    void borrarCupon(String idCupon) throws Exception;

    /**
     * 🔹 Redimir un cupón por parte de un cliente.
     *
     * @param redimirCuponDTO datos necesarios para la redención
     * @return true si el cupón se redime correctamente
     * @throws Exception si el cupón no es válido, está vencido o ya fue usado
     */
    boolean redimirCupon(RedimirCuponDTO redimirCuponDTO) throws Exception;

    /**
     * 🔹 Listar todos los cupones registrados en el sistema.
     *
     * @return lista resumida de cupones
     */
    List<ItemCuponDTO> listarCupones();

    /**
     * 🔹 Listar los cupones asociados a un cliente específico.
     *
     * @param listarCuponDTO datos del cliente
     * @return lista de cupones del cliente
     */
    List<ItemCuponDTO> listarCuponesCliente(ListarCuponDTO listarCuponDTO);
}
