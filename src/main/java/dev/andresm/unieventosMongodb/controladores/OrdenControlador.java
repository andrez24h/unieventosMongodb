package dev.andresm.unieventosMongodb.controladores;

import dev.andresm.unieventosMongodb.dto.conex.MensajeDTO;
import dev.andresm.unieventosMongodb.dto.orden.CrearOrdenDTO;
import dev.andresm.unieventosMongodb.dto.orden.ItemOrdenDTO;
import dev.andresm.unieventosMongodb.dto.orden.OrdenDetalleDTO;
import dev.andresm.unieventosMongodb.servicios.interfaces.OrdenServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orden")
public class OrdenControlador {

    private final OrdenServicio ordenServicio;

    /**
     * Crear una nueva orden.
     */

    @PostMapping("/crear")
    public ResponseEntity<MensajeDTO<String>> crearOrden(
            @Valid @RequestBody CrearOrdenDTO crearOrdenDTO
    ) throws Exception {

        String id = ordenServicio.crearOrden(crearOrdenDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new MensajeDTO<>(
                        false,
                        "Orden creada correctamente",
                        id
                ));
    }

    /**
     * Obtener una orden por ID.
     */
    @GetMapping("/obtener/{idOrden}")
    public ResponseEntity<MensajeDTO<Object>> obtenerOrden(
            @PathVariable String idOrden
    ) throws Exception {

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Orden obtenida correctamente",
                        ordenServicio.obtenerOrden(idOrden)
                )
        );
    }

    /**
     * Obtener detalle completo de una orden.
     */
    @GetMapping("/detalle/{idOrden}")
    public ResponseEntity<MensajeDTO<OrdenDetalleDTO>> obtenerDetalleOrden(
            @PathVariable String idOrden
    ) throws Exception {

        OrdenDetalleDTO detalle = ordenServicio.obtenerItemsOrden(idOrden);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Detalle de orden obtenido correctamente",
                        detalle
                )
        );
    }

    /**
     * Listar órdenes por usuario.
     */
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<MensajeDTO<List<ItemOrdenDTO>>> listarOrdenesPorUsuario(
            @PathVariable String idUsuario
    ) throws Exception {

        List<ItemOrdenDTO> lista = ordenServicio.listarOrdenesPorUsuario(idUsuario);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Órdenes del usuario obtenidas correctamente",
                        lista
                )
        );
    }

    /**
     * Listar órdenes por evento.
     */
    @GetMapping("/evento/{idEvento}")
    public ResponseEntity<MensajeDTO<List<ItemOrdenDTO>>> listarOrdenesPorEvento(
            @PathVariable String idEvento
    ) throws Exception {

        List<ItemOrdenDTO> lista = ordenServicio.listarOrdenesPorEvento(idEvento);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Órdenes del evento obtenidas correctamente",
                        lista
                )
        );
    }

    /**
     * Verificar si el cliente realiza su primera compra.
     */
    @GetMapping("/primera-compra/{idCliente}")
    public ResponseEntity<MensajeDTO<Boolean>> esPrimeraCompra(
            @PathVariable String idCliente
    ) {

        boolean respuesta = ordenServicio.esPrimeraCompra(idCliente);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Consulta realizada correctamente",
                        respuesta
                )
        );
    }
}
