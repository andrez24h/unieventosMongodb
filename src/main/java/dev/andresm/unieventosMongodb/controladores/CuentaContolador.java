package dev.andresm.unieventosMongodb.controladores;

import dev.andresm.unieventosMongodb.dto.carrito.CarritoDTO;
import dev.andresm.unieventosMongodb.dto.conex.MensajeDTO;
import dev.andresm.unieventosMongodb.dto.cuenta.*;
import dev.andresm.unieventosMongodb.dto.cupon.RedimirCuponDTO;
import dev.andresm.unieventosMongodb.servicios.interfaces.CuentaServicio;
import dev.andresm.unieventosMongodb.servicios.interfaces.CuponServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cuenta")
public class CuentaContolador {
    private final CuentaServicio cuentaServicio;
    private final CuponServicio cuponServicio;

    // ================================
    // 👤 CUENTA
    // ================================

    // 1. Listar cuentas
    @GetMapping("/listar-todo")
    public ResponseEntity<MensajeDTO<List<ItemCuentaDTO>>> listarCuentas() {
        List<ItemCuentaDTO> lista = cuentaServicio.listarCuentas();
        return ResponseEntity.ok(new MensajeDTO<>(false, "Lista de cuentas", lista));
    }

    // 2. Obtener información
    @GetMapping("/obtener/{id}")
    public ResponseEntity<MensajeDTO<InformacionCuentaDTO>> obtenerInformacionCuenta(@PathVariable String id) throws Exception {
        InformacionCuentaDTO info = cuentaServicio.obtenerInformacionCuenta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Información de la cuenta", info));
    }

    // 3. Actualizar perfil
    @PutMapping("/actualizar-perfil")
    public ResponseEntity<MensajeDTO<String>> actualizarCuenta(@Valid @RequestBody ActualizarCuentaDTO cuenta) throws Exception {
        String id = cuentaServicio.actualizarCuenta(cuenta);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta actualizada exitosamente", id));
    }

    // 4. Eliminar cuenta
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCuenta(@PathVariable String id) throws Exception {
        cuentaServicio.eliminarCuenta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta eliminada exitosamente", id));
    }

    // ================================
    // 🛒 CARRITO
    // ================================

    // 5. Agregar evento
    @PostMapping("/carrito-agregar")
    public ResponseEntity<MensajeDTO<String>> agregarEventoCarrito(@Valid @RequestBody AgregarEventoDTO agregarEventoDTO) throws Exception {
        String resultado = cuentaServicio.agregarEventoCarrito(agregarEventoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, resultado, null));
    }

    // 6. Editar evento
    @PutMapping("/carrito-editar")
    public ResponseEntity<MensajeDTO<String>> editarEventoCarrito(@Valid @RequestBody EditarEventoCarritoDTO editarEventoCarritoDTO) throws Exception {
        String resultado = cuentaServicio.editarEventoCarrito(editarEventoCarritoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, resultado, null));
    }

    // 7. Eliminar evento
    @DeleteMapping("/carrito-eliminar")
    public ResponseEntity<MensajeDTO<String>> eliminarEventoCarrito(@Valid @RequestBody EliminarEventoDTO eliminarEventoDTO) throws Exception {
        String resultado = cuentaServicio.eliminarEventoCarrito(eliminarEventoDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, resultado, null));
    }

    // 8. Vaciar carrito
    @DeleteMapping("/carrito-vaciar/{idCliente}")
    public ResponseEntity<MensajeDTO<String>> vaciarEventoCarrito(@PathVariable String idCliente) throws Exception {
        String resultado = cuentaServicio.vaciarEventoCarrito(idCliente);
        return ResponseEntity.ok(new MensajeDTO<>(false, resultado, null));
    }

    // 9. Obtener carrito
    @GetMapping("/carrito/{idCliente}")
    public ResponseEntity<MensajeDTO<CarritoDTO>> obtenerEventoCarrito(@PathVariable String idCliente) throws Exception {
        CarritoDTO carritoDTO = cuentaServicio.obtenerEventoCarrito(idCliente);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Carrito obtenido correctamente", carritoDTO));
    }

    // ================================
    // 🎟 CUPONES
    // ================================

    // 10. Redimir cupón
    @PostMapping("/redimir-cupon")
    public ResponseEntity<MensajeDTO<String>> redimirCupon(@Valid @RequestBody RedimirCuponDTO redimirCuponDTO) throws Exception {
        boolean redimido = cuponServicio.redimirCupon(redimirCuponDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, redimido ? "Cupón redimido exitosamente" : "No se pudo redimir el cupón", null));
    }
}