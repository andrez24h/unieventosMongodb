package dev.andresm.unieventosMongodb.test;

import dev.andresm.unieventosMongodb.documentos.*;
import dev.andresm.unieventosMongodb.dto.evento.*;
import dev.andresm.unieventosMongodb.repositorios.EventoRepo;
import dev.andresm.unieventosMongodb.servicios.interfaces.EventoServicio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class EventoServicioTest {

    @Autowired
    private EventoServicio eventoServicio;

    @Autowired
    private EventoRepo eventoRepo;

    /**
     * Prueba para crear un evento real en MongoDB.

     * Validaciones:
     * 1. No debe lanzar excepción.
     * 2. Debe retornar un ID válido.
     * 3. Permite visualizar el objeto en Mongo Express.
     */
    @Test
    public void crearEventoTest() {

        // 1. Crear localidades usando Builder
        List<Localidad> localidades = List.of(
                Localidad.builder()
                        .nombre("VIP")
                        .precio(250000)
                        .capacidadMaxima(100)
                        .entradasVendidas(0)
                        .porcentajeVenta(0)
                        .build(),

                Localidad.builder()
                        .nombre("GENERAL")
                        .precio(150000)
                        .capacidadMaxima(200)
                        .entradasVendidas(0)
                        .porcentajeVenta(0)
                        .build(),

                Localidad.builder()
                        .nombre("PLATEA")
                        .precio(185000)
                        .capacidadMaxima(155)
                        .entradasVendidas(0)
                        .porcentajeVenta(0)
                        .build()
        );

        // 2. Crear DTO con Builder
        CrearEventoDTO dto = CrearEventoDTO.builder()
                .idUsuario("6a3bf03bebe52f1664cf6fe4") // 👈 ID REAL
                .nombre("Festival Rock Armenia 2026")
                .direccion("Centro de Convenciones Armenia")
                .ciudad("Armenia")
                .descripcion("Festival de rock con bandas nacionales")
                .imagenPortada("rock-portada.jpg")
                .imagenLocalidades("rock-localidades.jpg")
                .tipo(TipoEvento.CONCIERTO)
                .estado(EstadoEvento.ACTIVO)
                .ubicacion(new Ubicacion(4.53, -75.67))
                .fecha(LocalDateTime.now().plusDays(10))
                .localidades(localidades)
                .build();

        // 3. Ejecutar servicio
        Assertions.assertDoesNotThrow(() -> {

            // 4. Crear evento
            String idEvento = eventoServicio.crearEvento(dto);

            // 5. Debug
            System.out.println("ID EVENTO CREADO: " + idEvento);
        });
    }

    /**
     * Prueba para editar un evento existente.

     * Nota:
     * Debes copiar un ID real desde Mongo Express.
     */
    @Test
    public void editarEventoTest() {

        // ⚠️ REEMPLAZA POR ID REAL
        String idEvento = "69f192114913552c261075f4";

        // 🔴 DEBUG AQUI
        Optional<Evento> evento = eventoRepo.buscarId(idEvento);
        System.out.println("RESULTADO: " + evento);

        // 1. Crear nuevas localidades
        List<Localidad> localidades = List.of(

                Localidad.builder()
                        .nombre("VIP")
                        .precio(300000)
                        .capacidadMaxima(80)
                        .entradasVendidas(0)
                        .porcentajeVenta(0)
                        .build()
        );

        // 2. Crear DTO
        EditarEventoDTO dto = EditarEventoDTO.builder()
                .id(idEvento)
                .nombre("Concierto METALLICA")
                .ciudad("Armenia")
                .direccion("Nchalet")
                .descripcion("XXXXXXXXXXXXXX")
                .imagenPortada("imgEdit.jpg")
                .imagenLocalidades("imgEdit2.jpg")
                .tipo(TipoEvento.CONCIERTO)
                .estado(EstadoEvento.ACTIVO)
                .ubicacion(new Ubicacion(4.5, -75.6))
                .fecha(LocalDateTime.now().plusDays(5))
                .localidades(localidades)
                .build();

        // 3. Ejecutar servicio
        Assertions.assertDoesNotThrow(() -> {
            String resultado = eventoServicio.editarEvento(dto);
            System.out.println("EVENTO EDITADO: " + resultado);
        });
    }

    /**
     * Prueba para eliminar un evento.
     */
    @Test
    public void eliminarEventoTest() {

        // ⚠️ REEMPLAZAR
        String idEvento = "PEGAR_ID_AQUI";

        Assertions.assertDoesNotThrow(() -> {

            String resultado = eventoServicio.eliminarEvento(idEvento);

            System.out.println("EVENTO ELIMINADO: " + resultado);
        });
    }

    /**
     * Prueba para obtener información completa del evento.
     */
    @Test
    public void obtenerInformacionEventoTest() {

        String idEvento = "69f192114913552c261075f4";

        Assertions.assertDoesNotThrow(() -> {

            InformacionEventoDTO evento = eventoServicio.obtenerInformacionEvento(idEvento);

            System.out.println("EVENTO INFO: " + evento);
        });
    }

    /**
     * Prueba para listar eventos.
     */
    @Test
    public void listarEventosTest() {

        List<ItemEventoDTO> lista = eventoServicio.listarEventos();

        // Debug
        System.out.println("LISTA EVENTOS: " + lista);

        Assertions.assertNotNull(lista);
    }

    /**
     * Prueba de disponibilidad de localidades.
     */
    @Test
    public void disponibilidadTest() {

        String idEvento = "69f192114913552c261075f4";

        DisponibilidadEventoDTO dto = new DisponibilidadEventoDTO(
                idEvento,
                "VIP",
                2
        );

        Assertions.assertDoesNotThrow(() -> {

            boolean disponible = eventoServicio.disponibilidad(dto);

            System.out.println("DISPONIBILIDAD: " + disponible);
        });
    }

    /**
     * Prueba de filtrado de eventos.
     */
    @Test
    public void filtrarEventosTest() {

        FiltroEventoDTO filtro = new FiltroEventoDTO(
                "Concierto",
                TipoEvento.CONCIERTO,
                "Armenia",
                null
        );

        Assertions.assertDoesNotThrow(() -> {

            List<ItemEventoDTO> lista = eventoServicio.filtrarEventos(filtro);

            System.out.println("EVENTOS FILTRADOS: " + lista);
        });
    }
}
