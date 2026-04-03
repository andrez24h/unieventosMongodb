package dev.andresm.unieventosMongodb.controladores;

import dev.andresm.unieventosMongodb.dto.conex.MensajeDTO;
import dev.andresm.unieventosMongodb.dto.cuenta.*;
import dev.andresm.unieventosMongodb.servicios.interfaces.CuentaServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AutenticacionControlador {
    private final CuentaServicio cuentaServicio;

    // 1. Crear cuenta
    @PostMapping("/crear-cuenta")
    public ResponseEntity<MensajeDTO<String>> crearCuenta(@Valid @RequestBody CrearCuentaDTO cuenta) throws Exception {
        String id = cuentaServicio.crearCuenta(cuenta);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Cuenta creada exitosamente", id));
    }

    // 2. Activar cuenta
    @PostMapping("/activar-cuenta")
    public ResponseEntity<MensajeDTO<String>> activarCuenta(@Valid @RequestBody ActivarCuentaDTO activarCuentaDTO) throws Exception {
        boolean activada = cuentaServicio.activarCuenta(activarCuentaDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, activada ? "Cuenta activada exitosamente" : "Error activando la cuenta", null));
    }

    // 3. Iniciar sesión
    @PostMapping("/iniciar-sesion")
    public ResponseEntity<MensajeDTO<TokenDTO>> iniciarSesion(@Valid @RequestBody LoginDTO loginDTO) throws Exception {
        TokenDTO token = cuentaServicio.iniciarSesion(loginDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, "Inicio de sesión exitoso", token));
    }

    // 4. Cambiar password
    @PostMapping("/cambiar-password")
    public ResponseEntity<MensajeDTO<String>> cambiarPassword(@Valid @RequestBody CambiarPasswordDTO cambiarPasswordDTO) throws Exception {
        String resultado = cuentaServicio.cambiarPassword(cambiarPasswordDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, resultado, null));

    }

    // 5. Enviar código de recuperación
    @PostMapping("/recuperar-password")
    public ResponseEntity<MensajeDTO<String>> enviarCodigoRecuperacion(@Valid @RequestBody CodigoPasswordDTO codigoPasswordDTO) throws Exception {
        String resultado = cuentaServicio.enviarCodigoRecuperacionPassword(codigoPasswordDTO);
        return ResponseEntity.ok(new MensajeDTO<>(false, resultado, null));
    }
}
