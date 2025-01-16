package dev.andresm.test;

import dev.andresm.dto.cuenta.CrearCuentaDTO;
import dev.andresm.modelo.Cuenta;
import dev.andresm.repositorios.CuentaRepo;
import dev.andresm.servicios.CuentaServicio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class CuentaServicioTest {

    @Autowired
    private CuentaServicio cuentaServicio;

    @Autowired
    private CuentaRepo cuentaRepo;
    /**
     * Prueba unitaria para validar la creación de una cuenta utilizando el patrón Builder.
     * Esta prueba asegura que:
     * 1. No se lancen excepciones durante la creación de la cuenta.
     * 2. El ID generado al crear la cuenta no sea nulo.
     */

    @Test
    public void crearCuentaTest() throws Exception {

        // Se usa el patrón Builder para crear CrearCuentaDTO.
        CrearCuentaDTO crearCuentaDTO = CrearCuentaDTO.builder()

                .cedula("9736736")
                .nombre("Andres")
                .email("amhernandezp@uqvirtual.edu.co")
                .password("12345")
                .telefonos(List.of("3117188224", "3105862354") )
                .build();

        // Llamada al servicio para crear la cuenta.
        String id = cuentaServicio.crearCuenta(crearCuentaDTO);

        // Asegurar que el ID no sea nulL.
        Assertions.assertNotNull(id, "El ID generado no debe ser nulo.");

        // Se implementa el método obtenerCuenta del servicio directamente.
        Cuenta guardado = obtenerCuenta(id);

        // Se verifica que el valor actual sea el mismo que el valor esperado
        Assertions.assertEquals("Andres", guardado.getUsuario().getNombre(), "El nombre no coincide.");
    }

    private Cuenta obtenerCuenta(String id) throws Exception {

        // Buscar la cuenta en el repositorio por ID
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById(id);

        // Si no se encuentra, lanzar una excepción
        if (cuentaOptional.isEmpty()) {

            throw new Exception("La cuenta con el ID: " + id + " no existe.");
        }

        // Retornar la cuenta si existe
        return cuentaOptional.get();
    }
}


