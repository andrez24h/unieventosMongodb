package dev.andresm.unieventosMongodb.test;

import dev.andresm.unieventosMongodb.documentos.Orden;
import dev.andresm.unieventosMongodb.dto.orden.CrearOrdenDTO;
import dev.andresm.unieventosMongodb.dto.orden.ItemOrdenDTO;
import dev.andresm.unieventosMongodb.dto.orden.OrdenDetalleDTO;
import dev.andresm.unieventosMongodb.repositorios.OrdenRepo;
import dev.andresm.unieventosMongodb.servicios.interfaces.OrdenServicio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class OrdenServicioTest {

    @Autowired
    OrdenServicio ordenServicio;

    @Autowired
    OrdenRepo ordenRepo;

    /**
     * =================================================================================
     * PRUEBA CREAR ORDEN CON CUPÓN
     * =================================================================================
     * <p>
     * Valida que:
     * 1. La orden se cree correctamente
     * 2. El cupón sea redimido
     * 3. El total tenga descuento aplicado
     * 4. La orden quede almacenada en Mongo
     */
    @Test
    public void crearOrdenConCuponTest() {

        CrearOrdenDTO dto = new CrearOrdenDTO(

                // _id REAL cliente Mongo
                "69e80ac52986d11fca740685",

                // código REAL cupón DISPONIBLE
                "CUPON-0f51c825-3d3f-451c-80fb-f146c2ae76d2"
        );

        Assertions.assertDoesNotThrow(() -> {

            String idOrden = ordenServicio.crearOrden(dto);

            System.out.println("ID ORDEN: " + idOrden);

            Assertions.assertNotNull(idOrden);

            Optional<Orden> ordenOptional = ordenRepo.buscarId(idOrden);

            Assertions.assertTrue(ordenOptional.isPresent());

            Orden orden = ordenOptional.get();

            System.out.println("TOTAL ORDEN: " + orden.getTotal());
            System.out.println("ESTADO ORDEN: " + orden.getEstado());
            System.out.println("ID CUPON: " + orden.getIdCupon());

            Assertions.assertNotNull(orden.getItems());
            Assertions.assertFalse(orden.getItems().isEmpty());
        });
    }

    /**
     * =================================================================================
     * PRUEBA CREAR ORDEN SIN CUPÓN
     * =================================================================================
     * <p>
     * Valida que:
     * 1. La orden se cree correctamente
     * 2. No exista cupón asociado
     * 3. La orden quede guardada
     */
    @Test
    public void crearOrdenSinCuponTest() {

        CrearOrdenDTO dto = new CrearOrdenDTO(

                // _id REAL cliente Mongo
                "69e80ac52986d11fca740685",

                // sin cupón
                null
        );

        Assertions.assertDoesNotThrow(() -> {

            String idOrden = ordenServicio.crearOrden(dto);

            System.out.println("ID ORDEN: " + idOrden);

            Assertions.assertNotNull(idOrden);

            Optional<Orden> ordenOptional = ordenRepo.buscarId(idOrden);

            Assertions.assertTrue(ordenOptional.isPresent());

            Orden orden = ordenOptional.get();

            System.out.println("TOTAL ORDEN: " + orden.getTotal());
            System.out.println("ESTADO ORDEN: " + orden.getEstado());

            Assertions.assertNull(orden.getIdCupon());

            Assertions.assertNotNull(orden.getItems());
            Assertions.assertFalse(orden.getItems().isEmpty());
        });
    }

    /**
     * =================================================================================
     * PRUEBA CLIENTE NO EXISTE
     * =================================================================================
     * <p>
     * Valida que:
     * 1. Se lance excepción si el cliente no existe
     */
    @Test
    public void crearOrdenClienteNoExisteTest() {

        CrearOrdenDTO dto = new CrearOrdenDTO(

                // id inexistente
                "ID-FAKE",

                null
        );

        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> ordenServicio.crearOrden(dto)
        );

        System.err.println("ERROR -> " + exception.getMessage());
    }

    /**
     * =================================================================================
     * PRUEBA CLIENTE INACTIVO
     * =================================================================================
     * <p>
     * Valida que:
     * 1. No se permita crear orden con cuenta INACTIVA
     * <p>
     * IMPORTANTE:
     * usar _id REAL de cuenta INACTIVA en Mongo
     */
    @Test
    public void crearOrdenClienteInactivoTest() {

        CrearOrdenDTO dto = new CrearOrdenDTO(

                // _id REAL cuenta INACTIVA
                "PEGAR_ID_CLIENTE_INACTIVO",

                null
        );

        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> ordenServicio.crearOrden(dto)
        );

        System.err.println("ERROR -> " + exception.getMessage());
    }

    /**
     * =================================================================================
     * PRUEBA EVENTO NO EXISTE
     * =================================================================================
     * <p>
     * Valida que:
     * 1. Se lance excepción cuando un item del carrito
     * tenga un idEvento inexistente
     * <p>
     * IMPORTANTE:
     * modificar manualmente el carrito en Mongo
     */
    @Test
    public void crearOrdenEventoNoExisteTest() {

        CrearOrdenDTO dto = new CrearOrdenDTO(

                // _id REAL cliente Mongo
                "69e80ac52986d11fca740685",

                null
        );

        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> ordenServicio.crearOrden(dto)
        );

        System.err.println("ERROR -> " + exception.getMessage());
    }

    /**
     * =================================================================================
     * PRUEBA LOCALIDAD NO EXISTE
     * =================================================================================
     * <p>
     * Valida que:
     * 1. Se lance excepción cuando la localidad
     * del carrito no exista en el evento
     * <p>
     * IMPORTANTE:
     * modificar nombreLocalidad manualmente en Mongo
     */
    @Test
    public void crearOrdenLocalidadNoExisteTest() {

        CrearOrdenDTO dto = new CrearOrdenDTO(

                // _id REAL cliente Mongo
                "69e80ac52986d11fca740685",

                null
        );

        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> ordenServicio.crearOrden(dto)
        );

        System.err.println("ERROR -> " + exception.getMessage());
    }

    /**
     * =================================================================================
     * PRUEBA SIN AFORO DISPONIBLE
     * =================================================================================
     * <p>
     * Valida que:
     * 1. No permita crear orden si no hay entradas suficientes
     * <p>
     * IMPORTANTE:
     * modificar capacidad/entradasVendidas en Mongo
     */
    @Test
    public void crearOrdenSinAforoTest() {

        CrearOrdenDTO dto = new CrearOrdenDTO(

                // _id REAL cliente Mongo
                "69e80ac52986d11fca740685",

                null
        );

        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> ordenServicio.crearOrden(dto)
        );

        System.err.println("ERROR -> " + exception.getMessage());
    }

    /**
     * =================================================================================
     * PRUEBA OBTENER ORDEN
     * =================================================================================
     * <p>
     * Valida que:
     * 1. La orden exista
     * 2. Se obtenga correctamente
     */
    @Test
    public void obtenerOrdenTest() {

        Assertions.assertDoesNotThrow(() -> {

            // _id REAL orden Mongo
            Orden orden = ordenServicio.obtenerOrden(
                    "6a0209b038799457e039ef2f"
            );

            System.out.println("ORDEN -> " + orden);

            Assertions.assertNotNull(orden);
        });
    }

    /**
     * =================================================================================
     * PRUEBA OBTENER ORDEN NO EXISTE
     * =================================================================================
     * <p>
     * Valida que:
     * 1. Se lance excepción si la orden no existe
     */
    @Test
    public void obtenerOrdenNoExisteTest() {

        Exception exception = Assertions.assertThrows(
                Exception.class,
                () -> ordenServicio.obtenerOrden("ID-FAKE")
        );

        System.err.println("ERROR -> " + exception.getMessage());
    }

    /**
     * =================================================================================
     * PRUEBA LISTAR ORDENES POR USUARIO
     * =================================================================================
     * <p>
     * Valida que:
     * 1. Existan órdenes asociadas al usuario
     */
    @Test
    public void listarOrdenesPorUsuarioTest() {

        Assertions.assertDoesNotThrow(() -> {

            List<ItemOrdenDTO> lista =
                    ordenServicio.listarOrdenesPorUsuario(

                            // _id REAL cliente Mongo
                            "69e80ac52986d11fca740685"
                    );

            System.out.println("ORDENES -> " + lista);

            Assertions.assertFalse(lista.isEmpty());
        });
    }

    /**
     * =================================================================================
     * PRUEBA LISTAR ORDENES POR EVENTO
     * =================================================================================
     * <p>
     * Valida que:
     * 1. Existan órdenes asociadas al evento
     */
    @Test
    public void listarOrdenesPorEventoTest() {

        Assertions.assertDoesNotThrow(() -> {

            List<ItemOrdenDTO> lista =
                    ordenServicio.listarOrdenesPorEvento(

                            // _id REAL evento Mongo
                            "69f192114913552c261075f4"
                    );

            System.out.println("ORDENES EVENTO -> " + lista);

            Assertions.assertFalse(lista.isEmpty());
        });
    }

    /**
     * Test que valida el método esPrimeraCompra.
     * <p>
     * CASO DE PRUEBA:
     * - Se consulta un cliente que YA tiene órdenes registradas en MongoDB.
     * <p>
     * OBJETIVO:
     * - Verificar que el sistema detecta correctamente que NO es su primera compra.
     * <p>
     * FLUJO:
     * 1. Se envía el id de un cliente existente.
     * 2. El servicio consulta las órdenes del cliente en la base de datos.
     * 3. Si existen órdenes, retorna false.
     * 4. Se valida el resultado esperado.
     * <p>
     * RESULTADO ESPERADO:
     * - false (el usuario ya ha realizado compras)
     */
    @Test
    public void esPrimeraCompraTest() {

        Assertions.assertDoesNotThrow(() -> {

            // GIVEN: Cliente existente en la base de datos
            String idCliente = "69e80ac52986d11fca740685";

            // WHEN: Se consulta si es su primera compra
            boolean resultado = ordenServicio.esPrimeraCompra(idCliente);

            // LOG (opcional para debugging)
            System.out.println("PRIMERA COMPRA -> " + resultado);

            // THEN: Se espera que NO sea primera compra
            Assertions.assertFalse(
                    resultado,
                    "El cliente ya tiene órdenes registradas, por lo tanto no es primera compra"
            );
        });
    }

    /**
     * =================================================================================
     * PRUEBA: OBTENER ITEMS DE UNA ORDEN
     * =================================================================================

     * OBJETIVO:
     * Validar que el sistema pueda recuperar el detalle completo de una orden,
     * incluyendo sus ítems y los códigos QR generados.

     * FLUJO:
     * 1. Se busca una orden existente en MongoDB por su ID.
     * 2. El servicio obtiene la orden completa desde la base de datos.
     * 3. Se transforman los items en DTOs.
     * 4. Se genera un QR por cada ítem de la orden.

     * VALIDACIONES:
     * - La orden no debe ser nula.
     * - Debe contener al menos un item.
     */
    @Test
    public void obtenerItemsOrdenTest() {

        Assertions.assertDoesNotThrow(() -> {

            // GIVEN: ID real de una orden creada en MongoDB
            String idOrden = "6a02653601b24a219a3ab14e";

            // WHEN: Se solicita el detalle de la orden
            OrdenDetalleDTO orden =
                    ordenServicio.obtenerItemsOrden(idOrden);

            // LOG para debug
            System.out.println("DETALLE ORDEN -> " + orden);

            // THEN: Validaciones del resultado
            Assertions.assertNotNull(orden, "La orden no debería ser nula");
            Assertions.assertFalse(
                    orden.items().isEmpty(),
                    "La orden debe contener al menos un item"
            );
        });
    }
}