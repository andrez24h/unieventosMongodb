package dev.andresm.test;

import dev.andresm.modelo.Cuenta;
import dev.andresm.modelo.EstadoCuenta;
import dev.andresm.modelo.Rol;
import dev.andresm.modelo.Usuario;
import dev.andresm.repositorios.CuentaRepo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class CuentaTest {

    @Autowired
    private CuentaRepo cuentaRepo;

    @Test
    public void crearCuentaTest(){

        // Se crea el usuario con sus propiedades.
        Usuario usuario = Usuario.builder()
                .cedula("9738738")
                .nombre("Andres")
                .telefonos(List.of("3117188224", "7408238"))
                .build();

        // Se crea la cuenta con sus propiedades incluyendo el usuario.
        Cuenta cuenta = Cuenta.builder()
                .email("amhernandezp@uqvirtual.edu.com")
                .estado(EstadoCuenta.INACTIVO)
                .fechaRegistro(LocalDateTime.now())
                .password("12345")
                .rol(Rol.CLIENTE)
                .usuario(usuario) // Asignar el usuario a la cuenta.
                .build();

        // Se guarda la cuenta.
        Cuenta guardada = cuentaRepo.save(cuenta);

        // Se verifica que se haya guardado la cuenta validando que no sea null.
        Assertions.assertNotNull(guardada);

        // Se verifica que el valor actual sea el mismo que el valor esperado.
        Assertions.assertEquals("Andres", guardada.getUsuario().getNombre());
    }

    @Test
    public void listarCuentaTest() {

        // Se obtiene la lista de todas los cuentas (por ahora solo tenemos 3).
        List<Cuenta> cuentas = cuentaRepo.findAll();

        // Se imprime las cuentas, se hace uso de una función lambda.
        cuentas.forEach(System.out::println);

        // Se verifica  la cantidad de objetos en la lista.
        Assertions.assertEquals(3, cuentas.size());
    }

    @Test
    public void actualizarCuentaTest() {

        // Se obtiene la Cuenta con el id "xxx" de la bd usando Optional.
        Optional<Cuenta> cuentaOptional = cuentaRepo.findById("67708cb94a0e925cca4b7773");

        // Se verifica que el cliente existe y se modifica.
        cuentaOptional.ifPresent(cuenta -> {

            // Se modifica el email de la Cuenta. Sí(el Optional no está vacío).
            cuenta.setEmail("claudia_nuevo@email.com");
            // Se guarda la cuenta.
            cuentaRepo.save(cuenta);
        });

        // Se obtiene la Cuenta con el id "xxx" de la bd nuevamente.
        Optional<Cuenta> cuentaActualizadaOptional = cuentaRepo.findById("67708cb94a0e925cca4b7773");

        // Se verifica que la Cuenta fue encontrada y el email se haya actualizado.
        cuentaActualizadaOptional.ifPresent(clienteActualizada ->
                Assertions.assertEquals("claudia_nuevo@email.com", clienteActualizada.getEmail())
        );
    }

    @Test
    public void eliminarCuentaTest() {

        // Se borra la cuenta con el id de la bd.
        cuentaRepo.deleteById("67708cb94a0e925cca4b7773");

        // Se obtiene la cuenta con el con el id de la bd.
        Optional<Cuenta> cuenta = cuentaRepo.findById("67708cb94a0e925cca4b7773");

        // Se verifica que la cuenta no exista (Optional.empty) yá que fue eliminada.
        Assertions.assertTrue(cuenta.isEmpty());
    }
}
