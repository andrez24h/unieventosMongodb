package dev.andresm.test;

import dev.andresm.dto.cuenta.*;
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
                .direccion("Armenia-rojasPinilla2")
                .email("andres24h@hotmail.com")
                .nombre("Andres")
                .password("12345")
                .telefonos(List.of("3117188224", "3105862354"))
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
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarId(id);

        // Si no se encuentra, lanzar una excepción
        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con el ID: " + id + " no existe.");
        }

        // Retornar la cuenta si existe
        return cuentaOptional.get();
    }

    @Test
    public void listarTest() {

        //Se obtiene la lista de las cuentas de los usuarios
        List<ItemCuentaDTO> lista = cuentaServicio.listarCuentas();

        //Se verifica que la lista no sea nula y que tenga 3 elementos (o los que hayan)
        Assertions.assertEquals(5, lista.size());

        // Imprime las cuentas en la consola
        lista.forEach(cuenta -> System.out.println("Cuenta: " + cuenta));
    }

    @Test
    public void actualizarCuentaTest() throws Exception {

        // Se define el id de la cuenta del usuario a actualizar, este id está en el dataset.js
        String idCuenta = "676dfaf9cd08a70ff6294c23";

        /*// Se obtiene la Cuenta con el id "xxx" de la bd usando Optional.
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById("678d8101d4284f2b6531cadf");*/


        // Se crea un objeto de tipo ActualizarCuentaDTO utilizando el patrón Builder
        ActualizarCuentaDTO actualizarCuentaDTO = ActualizarCuentaDTO.builder()

                .id(idCuenta)  // ID de la cuenta a actualizar
                .cedula("9736735")  // Nueva cédula
                .direccion("rojas3")  // Nueva dirección
                .email("andrez24h@gmail.com ")
                .nombre("abuelo")  // Nuevo nombre
                .telefonos(List.of("7408238", "7402965"))  // Nuevos teléfonos
                .build();

        //Se espera que no se lance ninguna excepción
        Assertions.assertDoesNotThrow(() -> {

            //Se actualiza la cuenta del usuario con el id definido
            cuentaServicio.actualizarCuenta(actualizarCuentaDTO);

            //Obtenemos el detalle de la cuenta del usuario con el id definido
            InformacionCuentaDTO detalle = cuentaServicio.obtenerInformacionCuenta(idCuenta);

            //Se verifica que la dirección del usuario sea la actualizada
            Assertions.assertEquals("rojas3", detalle.direccion());
        });
    }

    @Test
    public void eliminarCuentaTest() throws Exception {

        //Se define el id de la cuenta del usuario a eliminar, este id está en el dataset.js
        String idCuenta = "678d8101d4284f2b6531cadf";

        //Se elimina la cuenta del usuario con el id definido
        Assertions.assertDoesNotThrow(() -> cuentaServicio.eliminarCuenta(idCuenta));

        //Al intentar obtener la cuenta del usuario con el id definido se debe lanzar una excepción
        Assertions.assertThrows(Exception.class, () -> cuentaServicio.obtenerInformacionCuenta(idCuenta));
    }

    @Test
    public void iniciarSesionTest() {
        // Crear el DTO de login utilizando el patrón Builder
        LoginDTO loginDTO = LoginDTO.builder()

                .email("andres24h@hotmail.com")
                .password("12345")
                .build();

        // Ejecutar el método y verificar que no lanza excepciones
        Assertions.assertDoesNotThrow(() -> {
            TokenDTO tokenDTO = cuentaServicio.iniciarSesion(loginDTO);

            // Imprimir el objeto TokenDTO
            System.out.println("TokenDTO: " + tokenDTO);

            // Imprimir el token en sí
            System.out.println("Token: " + tokenDTO.token());

            Assertions.assertNotNull(tokenDTO); // Se verifica que el objeto tokenDTO no sea nulo, lo que indica que la autenticación fue exitosa.
            Assertions.assertTrue(tokenDTO.token().startsWith("ey"));  // Se verifica que el token empiece con "ey". Es un patrón común en los tokens JWT (JSON Web Tokens). Esto es una forma de asegurarse de que el token tiene el formato esperado.
        });
    }
}


