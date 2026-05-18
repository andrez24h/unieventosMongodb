package dev.andresm.unieventosMongodb.test;

import dev.andresm.unieventosMongodb.documentos.EstadoCuenta;
import dev.andresm.unieventosMongodb.documentos.Rol;
import dev.andresm.unieventosMongodb.documentos.Usuario;
import dev.andresm.unieventosMongodb.dto.carrito.CarritoDTO;
import dev.andresm.unieventosMongodb.dto.cuenta.*;
import dev.andresm.unieventosMongodb.documentos.Cuenta;
import dev.andresm.unieventosMongodb.repositorios.CuentaRepo;
import dev.andresm.unieventosMongodb.servicios.interfaces.CuentaServicio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
public class CuentaServicioTest {

    @Autowired
    private CuentaServicio cuentaServicio;

    @Autowired
    private CuentaRepo cuentaRepo;

    /**
     * Prueba unitaria para validar la creación de cuentas con rol ADMINISTRADOR.

     * Esta prueba asegura que:
     * 1. Se puedan crear múltiples administradores usando Builder.
     * 2. Se asignen correctamente los atributos (rol, estado, email, etc).
     * 3. Las cuentas se guarden correctamente en la base de datos.
     * 4. Se puedan recuperar posteriormente desde el repositorio.
     * 5. Se verifique la persistencia de cada administrador creado.
     */

    /**
     * Falta encriptar password con passwordEncoder
     */
    @Test
    public void crearAdministradoresTest() {

        // 1. Crear administrador 1 usando Builder
        Cuenta admin1 = Cuenta.builder()
                .id("admin1")
                .rol(Rol.ADMINISTRADOR)
                .estado(EstadoCuenta.ACTIVO)
                .email("admin1@unieventos.com")
                .password("123456") // 🔐 ideal: usar encriptación si ya la tienes
                .fechaRegistro(LocalDateTime.now())
                .usuario(
                        Usuario.builder()
                                .cedula("1001")
                                .nombre("Admin Uno")
                                .direccion("Calle 1")
                                .telefonos(List.of("1111111111"))
                                .build()
                )
                .build();

        // 2. Crear administrador 2 usando Builder
        Cuenta admin2 = Cuenta.builder()
                .id("admin2")
                .rol(Rol.ADMINISTRADOR)
                .estado(EstadoCuenta.ACTIVO)
                .email("admin2@unieventos.com")
                .password("123456")
                .fechaRegistro(LocalDateTime.now())
                .usuario(
                        Usuario.builder()
                                .cedula("1002")
                                .nombre("Admin Dos")
                                .direccion("Calle 2")
                                .telefonos(List.of("2222222222"))
                                .build()
                )
                .build();

        // 3. Crear administrador 3 usando Builder
        Cuenta admin3 = Cuenta.builder()
                .id("admin3")
                .rol(Rol.ADMINISTRADOR)
                .estado(EstadoCuenta.ACTIVO)
                .email("admin3@unieventos.com")
                .password("123456")
                .fechaRegistro(LocalDateTime.now())
                .usuario(
                        Usuario.builder()
                                .cedula("1003")
                                .nombre("Admin Tres")
                                .direccion("Calle 3")
                                .telefonos(List.of("3333333333"))
                                .build()
                )
                .build();

        // 4. Guardar administradores en la base de datos
        cuentaRepo.save(admin1);
        cuentaRepo.save(admin2);
        cuentaRepo.save(admin3);

        // 5. Verificar que cada administrador fue persistido correctamente
        Assertions.assertTrue(cuentaRepo.findById("admin1").isPresent());
        Assertions.assertTrue(cuentaRepo.findById("admin2").isPresent());
        Assertions.assertTrue(cuentaRepo.findById("admin3").isPresent());

        // 6. Imprimir resultado para verificación manual (DEBUG / consola)
        System.out.println("Admins creados correctamente");
    }

    /**
     * Prueba unitaria para validar la creación de una cuenta utilizando el patrón Builder.
     * Esta prueba asegura que:
     * 1. No se lancen excepciones durante la creación de la cuenta.
     * 2. El ID generado al crear la cuenta no sea nulo.
     * 3. Se genere correctamente un código de verificación para activar la cuenta.
     * 4. Se pueda visualizar el código en consola para pruebas tipo Postman.
     */
    @Test
    public void crearCuentaTest() throws Exception {

        // Se usa el patrón Builder para crear CrearCuentaDTO.
        CrearCuentaDTO crearCuentaDTO = CrearCuentaDTO.builder()

                .cedula("9737737")
                .direccion("Armenia")
                .email("andres24h@hotmail.com")
                .nombre("Hernandez")
                .password("12345")
                .telefonos(List.of("3117188224", "3105862354"))
                .build();

        // 1. Llamada al servicio para crear la cuenta.
        String id = cuentaServicio.crearCuenta(crearCuentaDTO);

        // 2. Asegurar que el ID no sea nulL.
        Assertions.assertNotNull(id, "El ID generado no debe ser nulo.");

        // 3. Obtener la cuenta desde la base de datos
        Cuenta guardado = cuentaServicio.obtenerCuenta(id);

        // 4. Imprimir el código de verificación en consola
        // Este código es el mismo que se envía por correo y se usa para activar la cuenta.
        System.out.println("CODIGO VERIFICACION: " +
                guardado.getCodigoValidacionRegistro().getCodigo());

        // 5. Validar que el nombre se haya guardado correctamente
        Assertions.assertEquals(
                "Hernandez",
                guardado.getUsuario().getNombre(),
                "El nombre no coincide."
        );

        // 6. Validar que el código de verificación exista
        Assertions.assertNotNull(
                guardado.getCodigoValidacionRegistro(),
                "Debe generarse un código de verificación"
        );
    }

    /**
     * Prueba unitaria para validar la activación de una cuenta.
     * Esta prueba asegura que:
     * 1. Se pueda activar una cuenta existente usando email y código de verificación.
     * 2. El servicio retorne true cuando la activación es exitosa.
     * 3. El estado de la cuenta cambie de INACTIVO a ACTIVO.
     * 4. El código de validación sea eliminado después de activar la cuenta.
     * 5. El flujo sea equivalente a una petición realizada desde Postman.
     */
    @Test
    public void activarCuentaTest() throws Exception {

        // 1. Construir el DTO con los datos necesarios
        ActivarCuentaDTO activarCuentaDTO = new ActivarCuentaDTO(
                "andres24h@hotmail.com",   // email real en tu BD
                      "sZwTIpyvMo"               // código real que viste en Mongo
        );

        // 2. Ejecutar el servicio de activación
        boolean resultado = cuentaServicio.activarCuenta(activarCuentaDTO);

        // 3. Validar que la activación fue exitosa
        Assertions.assertTrue(resultado, "La cuenta debería activarse correctamente");

        // 4. Obtener la cuenta actualizada desde la base de datos
        Cuenta cuenta = cuentaServicio.obtenerEmail("andrez24h@gmail.com");

        // 5. Verificar que el estado cambió a ACTIVO
        Assertions.assertEquals(EstadoCuenta.ACTIVO, cuenta.getEstado(), "La cuenta debe quedar ACTIVA");

        // 6. Verificar que el código de validación fue eliminado
        Assertions.assertNull(cuenta.getCodigoValidacionRegistro(), "El código debe eliminarse después de activar");
    }

    /**
     * Prueba unitaria para validar el inicio de sesión de una cuenta existente.
     * Esta prueba simula el comportamiento de un login desde Postman.

     * Esta prueba asegura que:
     * 1. Se pueda iniciar sesión con credenciales válidas (email y contraseña).
     * 2. No se lancen excepciones durante el proceso de autenticación.
     * 3. Se genere correctamente un token JWT.
     * 4. El token tenga un formato válido (empiece por "ey").
     * 5. Se pueda visualizar el token en consola para pruebas manuales.
     */
    @Test
    public void iniciarSesionTest() {

        // 1. Crear el DTO de login
        LoginDTO loginDTO = LoginDTO.builder()

                .email("andrez24h@gmail.com") // cuenta ya existente y activa en Mongo
                .password("12345")              // contraseña real
                .build();

        // 2. Ejecutar el método y validar que no lance excepciones
        Assertions.assertDoesNotThrow(() -> {

            // 3. Llamar al servicio de login
            TokenDTO tokenDTO = cuentaServicio.iniciarSesion(loginDTO);

            // 4. Imprimir el objeto completo
            System.out.println("TokenDTO: " + tokenDTO);

            // 5. Imprimir solo el token
            System.out.println("Token: " + tokenDTO.token());

            // 7. Validar formato básico de JWT
            Assertions.assertNotNull(tokenDTO); // Se verifica que el objeto tokenDTO no sea nulo, lo que indica que la autenticación fue exitosa.
            Assertions.assertTrue(tokenDTO.token().startsWith("ey"));  // Se verifica que el token empiece con "ey". Es un patrón común en los tokens JWT (JSON Web Tokens). Esto es una forma de asegurarse de que el token tiene el formato esperado.
        });
    }

    /**
     * Prueba unitaria para validar la actualización de una cuenta.

     * Esta prueba asegura que:
     * 1. Se pueda actualizar una cuenta existente mediante su ID.
     * 2. No se lancen excepciones durante el proceso.
     * 3. Los datos sean modificados correctamente en la base de datos.
     * 4. Se pueda obtener la información actualizada de la cuenta.
     * 5. Los cambios realizados (ej: dirección) coincidan con lo esperado.
     */
    @Test
    public void actualizarCuentaTest() throws Exception {

        // 1. ID de la cuenta existente en la BD
        String idCuenta = "69e7fbb7c6231e5d32f9ebf9";

        /*// Se obtiene la Cuenta con el id "xxx" de la bd usando Optional.
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById("678d8101d4284f2b6531cadf");*/


        // 2. Crear DTO con nuevos datos usando Builder
        ActualizarCuentaDTO actualizarCuentaDTO = ActualizarCuentaDTO.builder()

                .id(idCuenta)  // ID de la cuenta a actualizar
                .cedula("9738738")  // Nueva cédula
                .direccion("Circasia")  // Nueva dirección
                .email("andrez24h@gmail.com")
                .nombre("Piñeres")  // Nuevo nombre
                .telefonos(List.of("740823812345", "7402965"))  // Nuevos teléfonos
                .build();

        // 3. Ejecutar servicio y validar que no lance excepciones
        Assertions.assertDoesNotThrow(() -> {

            // 4. Actualizar la cuenta
            cuentaServicio.actualizarCuenta(actualizarCuentaDTO);

            // 5. Obtener información actualizada
            InformacionCuentaDTO detalle = cuentaServicio.obtenerInformacionCuenta(idCuenta);

            // 6. Imprimir resultado (DEBUG)
            System.out.println("Cuenta actualizada: " + detalle);

            // 7. Validar que el cambio se aplicó correctamente
            Assertions.assertEquals("Circasia", detalle.direccion());

            // 8. Validar otro campo (recomendado)
            Assertions.assertEquals("Piñeres", detalle.nombre());
        });
    }

    /**
     * Prueba unitaria para validar el envío de código de recuperación de contraseña.

     * Esta prueba asegura que:
     * 1. Se pueda enviar un código a un correo existente.
     * 2. No se lancen excepciones durante el proceso.
     * 3. El servicio retorne el mensaje esperado.
     * 4. Se pueda visualizar el código generado en consola (debug).
     */
    @Test
    public void enviarCodigoRecuperacionPasswordTest() {

        // 1. Crear DTO con email existente en BD
        CodigoPasswordDTO codigoPasswordDTO =
                new CodigoPasswordDTO("andrez24h@gmail.com");

        // 2. Ejecutar servicio y validar que no lance excepción
        Assertions.assertDoesNotThrow(() -> {

            // 3. Llamar al método
            String resultado = cuentaServicio.enviarCodigoRecuperacionPassword(codigoPasswordDTO);

            // 4. Imprimir resultado
            System.out.println("Resultado: " + resultado);

            // 5. Obtener cuenta y mostrar (DEBUG)
            Cuenta cuenta = cuentaServicio.obtenerEmail(codigoPasswordDTO.email());

            // 6. Imprimir código de recuperación generado (DEBUG)
            System.out.println("CODIGO RECUPERACION: " +
                    cuenta.getCodigoValidacionPassword().getCodigo());

            // 7. Validar respuesta esperada
            Assertions.assertEquals(
                    "Se ha enviado un correo con el código de recuperación",
                    resultado
            );
        });
    }

    /**
     * Prueba unitaria para validar el cambio de contraseña.

     * Esta prueba asegura que:
     * 1. Se pueda cambiar la contraseña con un código válido.
     * 2. No se lancen excepciones.
     * 3. El servicio retorne el mensaje correcto.
     */
    @Test
    public void cambiarPasswordTest() {

        // 1. Crear DTO con datos válidos
        CambiarPasswordDTO cambiarPasswordDTO = new CambiarPasswordDTO(
                "andrez24h@gmail.com",
                "BjNfM9dKc5", // Código real
                "pass"
        );

        // 2. Ejecutar y validar
        Assertions.assertDoesNotThrow(() -> {

            // 3. Llamar servicio
            String resultado = cuentaServicio.cambiarPassword(cambiarPasswordDTO);

            // 4. Imprimir resultado
            System.out.println("Resultado: " + resultado);

            // 5. Validar respuesta
            Assertions.assertEquals(
                    "Contraseña cambiada correctamente",
                    resultado
            );
        });
    }

    /**
     * Prueba unitaria para validar la obtención de una cuenta por ID
       usando directamente el servicio.

     * Esta prueba asegura que:
     * 1. Se pueda obtener una cuenta existente.
     * 2. No se lancen excepciones.
     * 3. La cuenta no sea nula.
     * 4. El ID coincida.
     */
    @Test
    public void obtenerCuentaTest() throws Exception {

        // 1. ID existente en BD
        String idCuenta = "69dbbd6b263f6f2215ff532d";

        // 2. Ejecutar servicio
        Cuenta cuenta = cuentaServicio.obtenerCuenta(idCuenta);

        // 3. AQUÍ lo imprimes
        System.out.println("Cuenta obtenida: " + cuenta);

        // 4. Validar que no sea nula
        Assertions.assertNotNull(cuenta);

        // 5. Validar ID
        Assertions.assertEquals(idCuenta, cuenta.getId());
    }

    @Test
    public void listarTest() {

        //Se obtiene la lista de las cuentas de los usuarios
        List<ItemCuentaDTO> lista = cuentaServicio.listarCuentas();

        //Se verifica que la lista no sea nula y que tenga 3 elementos (o los que hayan)
        Assertions.assertEquals(3, lista.size());

        // Imprime las cuentas en la consola
        lista.forEach(cuenta -> System.out.println("Cuenta: " + cuenta));
    }

    @Test
    public void eliminarCuentaTest() throws Exception {

        //Se define el id de la cuenta del usuario a eliminar, este id está en el dataset2.js
        String idCuenta = "69e80bb1e1172f4eef57c4d5";

        //Se elimina la cuenta del usuario con el id definido
        Assertions.assertDoesNotThrow(() -> cuentaServicio.eliminarCuenta(idCuenta));

        //Al intentar obtener la cuenta del usuario con el id definido se debe lanzar una excepción
        Assertions.assertThrows(Exception.class, () -> cuentaServicio.obtenerInformacionCuenta(idCuenta));
    }

    // =========================================================
    // CARRITO
    // =========================================================

    /**
     * Prueba unitaria para validar la adición de un evento al carrito.

     * Esta prueba asegura que:
     * 1. Se construya correctamente el DTO usando Builder.
     * 2. Se encuentre la cuenta y cumpla reglas de negocio.
     * 3. Se encuentre el evento y la localidad.
     * 4. Se agregue correctamente al carrito.
     * 5. No se lancen excepciones.
     * 6. Se retorne el mensaje esperado.
     */
    @Test
    public void agregarEventoCarritoTest() {

        // 1. Crear DTO con Builder (SIN ObjectId)
        AgregarEventoDTO dto = AgregarEventoDTO.builder()
                .cantidad(2)
                .nombreLocalidad("VIP")
                .idEvento("69f192114913552c261075f4")
                .idUsuario("69e80ac52986d11fca740685")
                .fecha(LocalDateTime.now())
                .build();

        // 2. Ejecutar servicio
        Assertions.assertDoesNotThrow(() -> {

            // 3. Llamar método
            String resultado = cuentaServicio.agregarEventoCarrito(dto);

            // 4. Imprimir resultado
            System.out.println("Resultado agregar: " + resultado);

            // 5. Validar respuesta
            Assertions.assertEquals(
                    "Evento agregado al carrito con éxito",
                    resultado
            );
        });
    }

    /**
     * Prueba unitaria para validar la edición de un evento en el carrito.

     * Esta prueba asegura que:
     * 1. Se construya correctamente el DTO.
     * 2. Se encuentre la cuenta y el carrito.
     * 3. Se encuentre el detalle dentro del carrito.
     * 4. Se actualicen cantidad y localidad.
     * 5. No se lancen excepciones.
     * 6. Se retorne el mensaje esperado.
     */
    @Test
    public void editarEventoCarritoTest() {

        // 1. Crear DTO (SIN ObjectId → String)
        EditarEventoCarritoDTO dto = EditarEventoCarritoDTO.builder()
                .idCliente("1010080936")
                .idDetalle("6708735bc560b73460c3adb6")
                .nuevaLocalidad("VIP")
                .nuevaCantidad(2)
                .build();

        // 2. Ejecutar servicio
        Assertions.assertDoesNotThrow(() -> {

            // 3. Llamar método
            String resultado = cuentaServicio.editarEventoCarrito(dto);

            // 4. Imprimir resultado
            System.out.println("Resultado editar: " + resultado);

            // 5. Validar respuesta
            Assertions.assertEquals(
                    "Evento del carrito editado con éxito",
                    resultado
            );
        });
    }

    /**
     * Prueba unitaria para validar la eliminación de un evento del carrito.

     * Esta prueba asegura que:
     * 1. Se construya correctamente el DTO.
     * 2. Se encuentre la cuenta y el carrito.
     * 3. Se encuentre el detalle a eliminar.
     * 4. Se elimine correctamente del carrito.
     * 5. No se lancen excepciones.
     * 6. Se retorne el mensaje esperado.
     */
    @Test
    public void eliminarEventoCarritoTest() {

        // 1. Crear DTO con Builder (SIN ObjectId)
        EliminarEventoDTO dto = EliminarEventoDTO.builder()
                .idCliente("1010080936")
                .idDetalle("67087310e8aed80268c9827c")
                .build();

        // 2. Ejecutar servicio
        Assertions.assertDoesNotThrow(() -> {

            // 3. Llamar método
            String resultado = cuentaServicio.eliminarEventoCarrito(dto);

            // 4. Imprimir resultado
            System.out.println("Resultado eliminar: " + resultado);

            // 5. Validar respuesta
            Assertions.assertEquals(
                    "Evento eliminado del carrito correctamente",
                    resultado
            );
        });
    }

    /**
     * Prueba unitaria para validar la obtención del carrito del cliente.

     * Esta prueba asegura que:
     * 1. Se encuentre la cuenta correctamente.
     * 2. Se valide que tenga rol CLIENTE.
     * 3. Se obtenga el carrito sin errores.
     * 4. El objeto retornado no sea nulo.
     */
    @Test
    public void obtenerEventoCarritoTest() throws Exception {

        // 1. ID cliente
        String idCliente = "1010080936";

        // 2. Ejecutar servicio
        CarritoDTO carrito = cuentaServicio.obtenerEventoCarrito(idCliente);

        // 3. Imprimir resultado
        System.out.println("Carrito obtenido: " + carrito);

        // 4. Validar resultado
        Assertions.assertNotNull(carrito);
    }

    /**
     * Prueba unitaria para validar el vaciado del carrito.

     * Esta prueba asegura que:
     * 1. Se encuentre la cuenta correctamente.
     * 2. Se valide el rol CLIENTE.
     * 3. Se eliminen todos los items del carrito.
     * 4. No se lancen excepciones.
     * 5. Se retorne el mensaje esperado.
     */
    @Test
    public void vaciarEventoCarritoTest() {

        // 1. ID cliente
        String idCliente = "1010080936";

        // 2. Ejecutar servicio
        Assertions.assertDoesNotThrow(() -> {

            // 3. Llamar método
            String resultado = cuentaServicio.vaciarEventoCarrito(idCliente);

            // 4. Imprimir resultado
            System.out.println("Resultado vaciar: " + resultado);

            // 5. Validar respuesta
            Assertions.assertEquals(
                    "Carrito vaciado correctamente",
                    resultado
            );
        });
    }
}
/**
 * return cuentaRepo.buscarId(id)
 *         .orElseThrow(() -> new Exception("La cuenta con el ID: " + id + " no existe."));
 */


/**
 * 0. se puede con builder,? lo veo raro cmo constryue el objeto
 * 1. solo envia la notificacion
 * 2.porque no imprime el codigo el metodo,?
 */