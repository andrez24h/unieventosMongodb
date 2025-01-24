package dev.andresm.servicios;

import dev.andresm.dto.cuenta.ActualizarCuentaDTO;
import dev.andresm.dto.cuenta.CrearCuentaDTO;
import dev.andresm.dto.cuenta.InformacionCuentaDTO;
import dev.andresm.dto.cuenta.ItemCuentaDTO;
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
}
