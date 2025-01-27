package dev.andresm.servicios;

import dev.andresm.dto.cuenta.TokenDTO;
import dev.andresm.dto.cuenta.*;
import dev.andresm.modelo.Cuenta;

import java.util.List;

public interface CuentaServicio {

    String crearCuenta(CrearCuentaDTO cuenta) throws Exception;

    List<ItemCuentaDTO> listarCuentas();

    String actualizarCuenta(ActualizarCuentaDTO cuenta) throws Exception;

    String eliminarCuenta(String id) throws Exception;

    InformacionCuentaDTO obtenerInformacionCuenta(String id) throws Exception;

    String generarCodigo();

    Cuenta obtenerCuenta(String id) throws Exception;

    Cuenta obtenerEmail(String email) throws Exception;

    TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception;
}
