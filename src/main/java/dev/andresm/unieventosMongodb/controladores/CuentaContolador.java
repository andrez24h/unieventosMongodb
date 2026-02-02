package dev.andresm.unieventosMongodb.controladores;

import dev.andresm.unieventosMongodb.dto.conex.MensajeDTO;
import dev.andresm.unieventosMongodb.dto.cuenta.ActualizarCuentaDTO;
import dev.andresm.unieventosMongodb.dto.cuenta.InformacionCuentaDTO;
import dev.andresm.unieventosMongodb.dto.cuenta.ItemCuentaDTO;
import dev.andresm.unieventosMongodb.servicios.interfaces.CuentaServicio;
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

    @GetMapping("/listar-todo")
    public ResponseEntity<MensajeDTO<List<ItemCuentaDTO>>> listarCuentas() {

        List<ItemCuentaDTO> lista = cuentaServicio.listarCuentas();
        return ResponseEntity.ok(new MensajeDTO<>(false, lista));
    }

    @PutMapping("/actualizar-perfil")
    public ResponseEntity<MensajeDTO<String>> actualizarCuenta(@Valid @RequestBody ActualizarCuentaDTO cuenta) throws Exception {

        cuentaServicio.actualizarCuenta(cuenta);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta actualizada exitosamente"));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<MensajeDTO<String>> eliminarCuenta(@PathVariable String id) throws Exception {

        cuentaServicio.eliminarCuenta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta eliminada exitosamente"));
    }

    @GetMapping("/obtener/{id}")
    public ResponseEntity<MensajeDTO<InformacionCuentaDTO>> obtenerInformacionCuenta(@PathVariable String id) throws Exception {

        InformacionCuentaDTO info = cuentaServicio.obtenerInformacionCuenta(id);
        return ResponseEntity.ok(new MensajeDTO<>(false, info));
    }
}
