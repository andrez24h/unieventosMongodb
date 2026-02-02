package dev.andresm.unieventosMongodb.servicios.interfaces;

import dev.andresm.unieventosMongodb.dto.cuenta.TokenDTO;
import dev.andresm.unieventosMongodb.dto.cuenta.*;
import dev.andresm.unieventosMongodb.documentos.Cuenta;

import java.util.List;

/**
 * 🔹 Servicio de gestión de cuentas.
 * - Define las operaciones relacionadas con:
 * - Registro y autenticación
 * - Activación y recuperación de cuenta
 * - Gestión de información del usuario
 * - Manejo del carrito de eventos
 */
public interface CuentaServicio {

    /**
     * 🔹 Crear una nueva cuenta de usuario.
     *
     * @param cuenta datos necesarios para crear la cuenta
     * @return mensaje de confirmación
     * @throws Exception si el correo ya existe o los datos son inválidos
     */
    String crearCuenta(CrearCuentaDTO cuenta) throws Exception;

    /**
     * 🔹 Generar un código aleatorio de validación.
     *
     * @return código generado como String
     */
    String generarCodigo();

    /**
     * 🔹 Activar una cuenta mediante código de verificación.
     *
     * @param activarCuentaDTO datos de activación
     * @return true si la cuenta se activa correctamente
     * @throws Exception si el código es inválido o la cuenta no existe
     */
    boolean activarCuenta(ActivarCuentaDTO activarCuentaDTO) throws Exception;

    /**
     * 🔹 Iniciar sesión en el sistema.
     *
     * @param loginDTO credenciales del usuario
     * @return token de autenticación
     * @throws Exception si las credenciales son incorrectas
     */
    TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception;

    /**
     * 🔹 Actualizar la información de una cuenta.
     *
     * @param cuenta datos actualizados
     * @return mensaje de confirmación
     * @throws Exception si la cuenta no existe o está inactiva
     */
    String actualizarCuenta(ActualizarCuentaDTO cuenta) throws Exception;

    /**
     * 🔹 Eliminar una cuenta del sistema.
     *
     * @param id identificador de la cuenta
     * @return mensaje de confirmación
     * @throws Exception si la cuenta no existe
     */
    String eliminarCuenta(String id) throws Exception;

    /**
     * 🔹 Listar todas las cuentas registradas.
     *
     * @return lista resumida de cuentas
     */
    List<ItemCuentaDTO> listarCuentas();

    /**
     * 🔹 Obtener información detallada de una cuenta.
     *
     * @param id identificador de la cuenta
     * @return información completa de la cuenta
     * @throws Exception si la cuenta no existe
     */
    InformacionCuentaDTO obtenerInformacionCuenta(String id) throws Exception;

    /**
     * 🔹 Enviar código de recuperación de contraseña al correo.
     *
     * @param email correo del usuario
     * @return mensaje de confirmación
     * @throws Exception si el correo no está registrado
     */
    String enviarCodigoRecuperacionPassword(CodigoPasswordDTO email) throws Exception;

    /**
     * 🔹 Cambiar la contraseña de una cuenta.
     *
     * @param cambiarPasswordDTO datos necesarios para el cambio
     * @return mensaje de confirmación
     * @throws Exception si el código es inválido o expiró
     */
    String cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws Exception;

    /**
     * 🔹 Obtener una cuenta por su ID.
     *
     * @param id identificador de la cuenta
     * @return entidad Cuenta
     * @throws Exception si la cuenta no existe
     */
    Cuenta obtenerCuenta(String id) throws Exception;

    /**
     * 🔹 Obtener una cuenta por su correo electrónico.
     *
     * @param email correo de la cuenta
     * @return entidad Cuenta
     * @throws Exception si la cuenta no existe
     */
    Cuenta obtenerEmail(String email) throws Exception;

    /**
     * 🔹 Agregar un evento al carrito del cliente.
     *
     * @param agregarEvento datos del evento a agregar
     * @return mensaje de confirmación
     * @throws Exception si la cuenta no es válida o el evento ya existe
     */
    String agregarEventoCarrito(AgregarEventoDTO agregarEvento) throws Exception;

    /**
     * 🔹 Editar un evento existente en el carrito.
     *
     * @param editarEventoCarritoDTO datos actualizados del evento
     * @return mensaje de confirmación
     * @throws Exception si el evento no existe en el carrito
     */
    String editarEventoCarrito(EditarEventoCarritoDTO editarEventoCarritoDTO) throws Exception;

    /**
     * 🔹 Eliminar un evento del carrito.
     *
     * @param eliminarEventoDTO datos del evento a eliminar
     * @return mensaje de confirmación
     * @throws Exception si el evento no existe en el carrito
     */
    String eliminarEventoCarrito(EliminarEventoDTO eliminarEventoDTO) throws Exception;
}