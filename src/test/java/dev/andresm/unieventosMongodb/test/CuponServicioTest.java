package dev.andresm.unieventosMongodb.test;

import dev.andresm.unieventosMongodb.documentos.Cupon;
import dev.andresm.unieventosMongodb.documentos.EstadoCupon;
import dev.andresm.unieventosMongodb.documentos.TipoCupon;
import dev.andresm.unieventosMongodb.repositorios.CuponRepo;
import dev.andresm.unieventosMongodb.dto.cupon.*;
import dev.andresm.unieventosMongodb.servicios.interfaces.CuponServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class CuponServicioTest {

    @Autowired
    private CuponRepo cuponRepo;

    @Autowired
    private CuponServicio cuponServicio;


    /**
     * =================================================================================
     * PRUEBA DE CREACIÓN DE CUPÓN ÚNICO
     * =================================================================================

     * Verifica que:
     * El cupón se cree correctamente
     * El código no sea null
     * El servicio no lance excepciones
     */

    @Test
    public void crearCuponUnicoTest() {

        CrearCuponDTO dto = new CrearCuponDTO(

                // 1. Código funcional del cupón
                // ERROR COMÚN:
                // No confundir con _id Mongo
                "CUPON-TEST-1",
                "Cupon Rock",
                20,
                LocalDateTime.now().plusDays(10),
                TipoCupon.UNICO,

                // 2. Cupón único NO usa beneficiarios
                // Mongo guardará []
                null
        );

        Assertions.assertDoesNotThrow(() -> {

            String codigo = cuponServicio.crearCupon(dto);

            System.out.println("CODIGO CUPON: " + codigo);

            Assertions.assertNotNull(codigo);
        });
    }

    /**
     * =================================================================================
     * PRUEBA DE CREACIÓN DE CUPÓN INDIVIDUAL
     * =================================================================================

     * Verifica que:
     * El cupón individual se cree correctamente
     * Se asignen beneficiarios
     * No se generen excepciones
     */
    @Test
    public void crearCuponIndividualTest() {

        List<String> beneficiarios = new ArrayList<>();

        // ID REAL DE CUENTA MONGO
        // 1. Debe ser un _id REAL de Cuenta Mongo
        // ERROR COMÚN:
        // No poner _id de Cupón
        beneficiarios.add("69e80ac52986d11fca740685");
        //  'OTRO_ID'
        //  'OTRO_ID'
        CrearCuponDTO dto = new CrearCuponDTO(

                // 2. Código funcional
                // No es _id Mongo
                "CUPON-IND-1",
                "Cupon VIP",
                30,
                LocalDateTime.now().plusDays(15),
                TipoCupon.INDIVIDUAL,

                // 3. Lista de cuentas autorizadas
                beneficiarios
        );

        Assertions.assertDoesNotThrow(() -> {

            String codigo = cuponServicio.crearCupon(dto);

            System.out.println("CODIGO CUPON INDIVIDUAL: " + codigo);

            Assertions.assertNotNull(codigo);
        });
    }

    /**
     * =================================================================================
     * PRUEBA DE ACTUALIZACIÓN DE CUPÓN
     * =================================================================================

     * Verifica que:
     * El cupón exista
     * La actualización se realice correctamente
     * El servicio no lance excepciones
     */
    @Test
    public void actualizarCuponTest() {

        ActualizarCuponDTO dto = new ActualizarCuponDTO(

                // 1. Debe ser el _id REAL del Cupón
                // ERROR COMÚN:
                // No poner codigo ni idCuenta
                "69fe0db092362d5eba2d3c4a",
                "Cupon Actualizado",
                95,
                LocalDateTime.now().plusDays(20),
                new ArrayList<>(),
                EstadoCupon.DISPONIBLE
        );

        Assertions.assertDoesNotThrow(() -> {

            String idActualizado = cuponServicio.actualizarCupon(dto);

            System.out.println("ID ACTUALIZADO: " + idActualizado);

            Assertions.assertEquals(dto.id(), idActualizado);
        });
    }

    /**
     * =================================================================================
     * PRUEBA DE BORRADO LÓGICO DE CUPÓN
     * =================================================================================

     * Verifica que:
     * El cupón cambie a estado NO_DISPONIBLE
     * El servicio no lance excepciones
     */
    @Test
    public void borrarCuponTest() {

        // 1. Debe ser el _id REAL del Cupón
        // ERROR COMÚN:
        // No usar codigo del cupón
        String idCupon = "PEGAR_ID_REAL_CUPON";

        Assertions.assertDoesNotThrow(() -> {

            cuponServicio.borrarCupon(idCupon);

            Optional<Cupon> cuponOptional = cuponRepo.buscarId(idCupon);

            Assertions.assertTrue(cuponOptional.isPresent());

            Cupon cupon = cuponOptional.get();

            System.out.println("ESTADO CUPON: " + cupon.getEstado());

            Assertions.assertEquals(
                    EstadoCupon.NO_DISPONIBLE,
                    cupon.getEstado()
            );
        });
    }

    /**
     * =================================================================================
     * PRUEBA DE REDENCIÓN DE CUPÓN
     * =================================================================================

     * Verifica que:
     * El cupón pueda redimirse
     * El servicio retorne true
     * No se generen excepciones
     */
    @Test
    public void redimirCuponTest() {

        RedimirCuponDTO redimirCuponDTO = new RedimirCuponDTO(

                // 1. Debe ser el CODIGO del cupón
                // ERROR COMÚN:
                // No usar _id Mongo
                "CUPON-14a7744f-1a03-4d97-8ed5-ef0f8e84d22f",

                // 2. Debe ser _id REAL de Cuenta Mongo
                // ERROR COMÚN:
                // Debe existir en beneficiarios[]
                "69e80ac52986d11fca740685"
        );

        Assertions.assertDoesNotThrow(() -> {

            boolean resultado = cuponServicio.redimirCupon(redimirCuponDTO);

            System.out.println("RESULTADO REDENCION: " + resultado);

            Assertions.assertTrue(resultado);
        });
    }

    /**
     * =================================================================================
     * PRUEBA DE LISTADO GENERAL DE CUPONES
     * =================================================================================

     * Verifica que:
     * La lista no sea null
     * Existan cupones disponibles
     */
    @Test
    public void listarCuponesTest() {

        Assertions.assertDoesNotThrow(() -> {

            List<ItemCuponDTO> lista = cuponServicio.listarCupones();

            System.out.println("LISTA CUPONES: " + lista);

            Assertions.assertNotNull(lista);

            Assertions.assertFalse(lista.isEmpty());
        });
    }

    /**
     * =================================================================================
     * PRUEBA DE LISTADO DE CUPONES POR CLIENTE
     * =================================================================================

     * Verifica que:
     * Se obtengan cupones asociados al cliente
     * La lista no sea null
     */
    @Test
    public void listarCuponesClienteTest() {

        ListarCuponDTO dto = new ListarCuponDTO(

                // 1. Debe ser _id REAL de Cuenta Mongo
                // ERROR COMÚN:
                // No usar _id de Cupón
                "69e80ac52986d11fca740685"
        );

        Assertions.assertDoesNotThrow(() -> {

            List<ItemCuponDTO> lista = cuponServicio.listarCuponesCliente(dto);

            System.out.println("CUPONES CLIENTE: " + lista);

            Assertions.assertNotNull(lista);
        });
    }

    /**
     * =================================================================================
     * PRUEBA DE CREACIÓN DE CUPÓN CON FECHA VENCIDA
     * =================================================================================

     * 1. Se intenta crear un cupón con fecha en el pasado
     * 2. Debe lanzarse excepción
     * 3. Se valida la regla de negocio:
     * "La fecha de vencimiento no puede ser en el pasado"
     */
    @Test
    public void crearCuponFechaVencidaTest() {

        CrearCuponDTO dto = new CrearCuponDTO(

                // código nuevo para evitar duplicados
                "CUPON-VENCIDO-1",

                // nombre del cupón
                "Cupon Expirado",

                // porcentaje descuento
                10,

                // fecha en el pasado
                LocalDateTime.now().minusDays(1),

                // tipo cupón
                TipoCupon.UNICO,

                // UNICO → beneficiarios null
                null
        );

        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> cuponServicio.crearCupon(dto)
        );
        System.err.println("ERROR -> " + exception.getMessage());
    }

    /**
     * =================================================================================
     * PRUEBA DE CREACIÓN DE CUPÓN CON CÓDIGO DUPLICADO
     * =================================================================================

     * 1. Se usa un código ya existente en Mongo
     * 2. Debe lanzarse excepción
     * 3. Se valida la regla:
     * "El código del cupón ya existe"
     */
    @Test
    public void crearCuponCodigoDuplicadoTest() {

        CrearCuponDTO dto = new CrearCuponDTO(

                // código REAL que ya exista en Mongo
                "CUPON-14a7744f-1a03-4d97-8ed5-ef0f8e84d22f",

                // nombre nuevo
                "Cupon Duplicado",

                // descuento
                25,

                // fecha válida
                LocalDateTime.now().plusDays(10),

                // tipo
                TipoCupon.UNICO,

                // UNICO → null
                null
        );

        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> cuponServicio.crearCupon(dto)
        );
        System.err.println("ERROR -> " + exception.getMessage());

        Assertions.assertEquals(
                "El código del cupón ya existe",
                exception.getMessage()
        );
    }

    /**
     * =================================================================================
     * PRUEBA DE REDENCIÓN DE CUPÓN INEXISTENTE
     * =================================================================================
     * <p>
     * 1. Se usa un código que NO exista en Mongo
     * 2. Debe lanzarse excepción
     * 3. Se valida:
     * "El cupón no existe"
     */
    @Test
    public void redimirCuponInexistenteTest() {

        RedimirCuponDTO dto = new RedimirCuponDTO(

                // código inexistente
                "CUPON-NO-EXISTE",

                // _id REAL de cuenta Mongo
                "69e80ac52986d11fca740685"
        );

        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> cuponServicio.redimirCupon(dto)
        );
        System.err.println("ERROR -> " + exception.getMessage());
    }

    /**
     * =================================================================================
     * PRUEBA DE REDENCIÓN CON CLIENTE NO AUTORIZADO
     * =================================================================================
     * <p>
     * 1. El cupón debe existir
     * 2. Debe ser INDIVIDUAL
     * 3. Debe estar DISPONIBLE
     * 4. El idCliente NO debe estar en beneficiarios[]
     * 5. Debe lanzarse:
     * "El cliente no cuenta con este cupón"
     */
    @Test
    public void redimirCuponNoAutorizadoTest() {

        RedimirCuponDTO dto = new RedimirCuponDTO(

                // código REAL de cupón INDIVIDUAL
                "CUPON-14a7744f-1a03-4d97-8ed5-ef0f8e84d22f",

                // _id REAL de otra cuenta Mongo
                // este id NO debe existir en beneficiarios[]
                "69fe11111111111111111111"
        );

        IllegalArgumentException exception = Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> cuponServicio.redimirCupon(dto)
        );
        System.err.println("ERROR -> " + exception.getMessage());
    }
}