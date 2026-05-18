package dev.andresm.unieventosMongodb.controladores;

import dev.andresm.unieventosMongodb.dto.conex.MensajeDTO;
import dev.andresm.unieventosMongodb.dto.cupon.ActualizarCuponDTO;
import dev.andresm.unieventosMongodb.dto.cupon.CrearCuponDTO;
import dev.andresm.unieventosMongodb.dto.cupon.ItemCuponDTO;
import dev.andresm.unieventosMongodb.dto.cupon.RedimirCuponDTO;
import dev.andresm.unieventosMongodb.dto.cupon.ListarCuponDTO;
import dev.andresm.unieventosMongodb.servicios.interfaces.CuponServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cupon")
public class CuponControlador {

    private final CuponServicio cuponServicio;

    // ================================
    //  CUPONES
    // ================================

    // 1. Crear cupón
    @PostMapping("/crear")
    public ResponseEntity<MensajeDTO<String>> crearCupon(@Valid @RequestBody CrearCuponDTO cuponDTO) throws Exception {
        String codigo = cuponServicio.crearCupon(cuponDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cupón creado exitosamente", codigo));
    }

    // 2. Actualizar cupón
    @PutMapping("/actualizar")
    public ResponseEntity<MensajeDTO<String>> actualizarCupon(@Valid @RequestBody ActualizarCuponDTO cuponDTO) throws Exception {
        String id = cuponServicio.actualizarCupon(cuponDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cupón actualizado exitosamente", id));
    }

    // 3. Eliminar cupón
    @DeleteMapping("/eliminar/{idCupon}")
    public ResponseEntity<MensajeDTO<String>> eliminarCupon(@PathVariable String idCupon) throws Exception {
        cuponServicio.borrarCupon(idCupon);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cupón eliminado exitosamente", idCupon));
    }

    // 4. Redimir cupón
    @PostMapping("/redimir")
    public ResponseEntity<MensajeDTO<String>> redimirCupon(@Valid @RequestBody RedimirCuponDTO redimirCuponDTO) throws Exception {
        boolean redimido = cuponServicio.redimirCupon(redimirCuponDTO);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        redimido ? "Cupón redimido exitosamente" : "No se pudo redimir el cupón",
                        null
                )
        );
    }

    // 5. Listar cupones disponibles
    @GetMapping("/listar")
    public ResponseEntity<MensajeDTO<List<ItemCuponDTO>>> listarCupones() {

        List<ItemCuponDTO> lista = cuponServicio.listarCupones();

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Lista de cupones",
                        lista
                )
        );
    }

    // 6. Listar cupones de un cliente
    @PostMapping("/listar-cliente")
    public ResponseEntity<MensajeDTO<List<ItemCuponDTO>>> listarCuponesCliente(@Valid @RequestBody ListarCuponDTO listarCuponDTO) {

        List<ItemCuponDTO> lista = cuponServicio.listarCuponesCliente(listarCuponDTO);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        "Lista de cupones del cliente",
                        lista
                )
        );
    }

    // 7. Verificar disponibilidad de cupón
    @GetMapping("/verificar/{codigoCupon}")
    public ResponseEntity<MensajeDTO<Boolean>> verificarDisponibilidadCupon(@PathVariable String codigoCupon) {

        boolean disponible = cuponServicio.verificarDisponibilidadCupon(codigoCupon);

        return ResponseEntity.ok(
                new MensajeDTO<>(
                        false,
                        disponible ? "Cupón disponible" : "Cupón no disponible",
                        disponible
                )
        );
    }
}
