package dev.andresm.servicios;

import com.mongodb.assertions.Assertions;
import dev.andresm.dto.cuenta.ActualizarCuentaDTO;
import dev.andresm.dto.cuenta.CrearCuentaDTO;
import dev.andresm.dto.cuenta.InformacionCuentaDTO;
import dev.andresm.dto.cuenta.ItemCuentaDTO;
import dev.andresm.dto.email.EmailDTO;
import dev.andresm.modelo.*;
import dev.andresm.repositorios.CuentaRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor

public class CuentaServicioImp implements CuentaServicio {

    private final CuentaRepo cuentaRepo;
    private final EmailServicio emailServicio;

    /**
     * Método para crear una nueva cuenta.
     * Este método verifica la existencia de un correo electrónico y una cédula antes de crear una nueva cuenta.
     * Utiliza el patrón Builder para construir el objeto `Cuenta`.
     *
     * @param cuenta Objeto de tipo CrearCuentaDTO que contiene los datos necesarios para crear una nueva cuenta.
     * @return El identificador único de la cuenta creada.
     * @throws Exception Si el correo o la cédula ya existen en el sistema.
     */
    @Override
    public String crearCuenta(CrearCuentaDTO cuenta) throws Exception {

        try {
            // Verificar si el correo ya existe
            if (existeEmail(cuenta.email())) {
                throw new Exception("El correo: " + cuenta.email() + " ya existe");
            }

            // Verificar si la cédula ya existe
            if (existeCedula(cuenta.cedula())) {
                throw new Exception("La cédula: " + cuenta.cedula() + " ya existe");
            }

            // Generar el código de validación para el registro.
            // Este código se usará para validar el registro del usuario.
            // La clase CodigoValidacion es una estructura para almacenar el código generado y la hora en la que fue creado.
            CodigoValidacion codigoValidacionRegistro = new CodigoValidacion(generarCodigo(), LocalDateTime.now());

            // Se encripta la contraseña antes de almacenarla en la bd.
            String passwordEncriptada = encriptarPassword(cuenta.password());

            // Construir la nueva cuenta utilizando el patrón de diseño Builder
            Cuenta nuevaCuenta = Cuenta.builder()
                    .carrito(new Carrito()) // Inicializar un carrito vacío si es necesario
                    .codigoValidacionPassword(null) // Inicializar como null (opcional, dependiendo del contexto)
                    .codigoValidacionRegistro(codigoValidacionRegistro)
                    .email(cuenta.email())
                    .estado(EstadoCuenta.INACTIVO)
                    .fechaRegistro(LocalDateTime.now())
                    .password(passwordEncriptada)
                    .rol(Rol.CLIENTE)
                    .usuario(Usuario.builder()
                            .cedula(cuenta.cedula() )
                            .direccion(cuenta.direccion() )
                            .nombre(cuenta.nombre() )
                            .telefonos(cuenta.telefonos() )
                            .build()
                    )
                    .build();

            // Enviar correo de activación
            // EmailDTO emailDTO = new EmailDTO(
            EmailDTO emailDTO = EmailDTO.builder()

                    .asunto("Su código de activación es: " + codigoValidacionRegistro.getCodigo() )
                    .contenido("Ingrese el código para poder activar su cuenta")
                    .destinatario(cuenta.email() )
                    .build();

            // El método enviarEmail se llama en esta línea.
            emailServicio.enviarEmail(emailDTO);

            // Guardar la nueva cuenta en el repositorio
            Cuenta cuentaCreada = cuentaRepo.save(nuevaCuenta);

            // Retornar el identificador de la cuenta creada.
            return cuentaCreada.getId(); // "Cuenta creada con éxito";

        } catch (Exception e) {

            // Manejar cualquier excepción que ocurra dentro del bloque try.
            // Aquí puedes registrar el error, lanzar una excepción personalizada o devolver un mensaje adecuado.
            e.printStackTrace(); // Registrar el error
            // Puedes lanzar una excepción más específica si lo deseas.
            throw new RuntimeException("Error al crear la cuenta: " + e.getMessage(), e);
        }
    }

    private boolean existeEmail(String email) {
        return cuentaRepo.buscarEmail(email).isPresent();
    }

    private boolean existeCedula(String cedula) {
        return cuentaRepo.buscarCedula(cedula).isPresent();
    }

    private String encriptarPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode( password );
    }

    @Override
    public String generarCodigo() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int tam = 10;
        SecureRandom random = new SecureRandom();
        StringBuilder codigo= new StringBuilder(tam);

        for (int i = 0; i < tam; i++) {

            int index = random.nextInt(caracteres.length());
            codigo.append(caracteres.charAt(index));
        }
        return codigo.toString();
    }

    @Override
    public List<ItemCuentaDTO> listarCuentas() {

        try {
            // Obtenemos todas las cuentas de los usuarios de la base de datos
            List<Cuenta> cuentas = cuentaRepo.findAll();

            // Se declara e inicializa una lista vacía llamada items.
            List<ItemCuentaDTO> items = new ArrayList<>();

            // Recorremos la lista de cuentas y por cada uno creamos un DTO y lo agregamos a la lista
            for (Cuenta cuenta : cuentas) {

                items.add(ItemCuentaDTO.builder()
                        .id(cuenta.getId())
                        .email(cuenta.getEmail())
                        .nombre(cuenta.getUsuario().getNombre())
                        .telefonos(cuenta.getUsuario().getTelefonos())
                        .build());
            }
            return items;

        } catch (Exception e) {

            // Capturamos cualquier error que ocurra durante la ejecución
            e.printStackTrace();  // Se puede registrar el error para depuración

            // Lanza una nueva excepción personalizada con el mensaje de error
            throw new RuntimeException("Error al listar las cuentas: " + e.getMessage(), e);
        }
    }

    @Override
    public String actualizarCuenta(ActualizarCuentaDTO cuenta) throws Exception {

        try {

            // Buscar la cuenta por su ID
            Optional<Cuenta> optionalCuenta = cuentaRepo.buscarId(cuenta.id());

            if (optionalCuenta.isEmpty()) {
                throw new Exception("No existe la cuenta con el ID: " + cuenta.id());
            }

            // Obtenemos la cuenta del usuario a modificar y actualizamos sus datos.
            Cuenta cuentaModificada = optionalCuenta.get();

           // Validar si la cédula ya existe en otra cuenta
            if (existeCedula(cuenta.cedula(), cuenta.id())) {
                throw new Exception("La cédula: " + cuenta.cedula() + " ya está registrada en otra cuenta.");
            }

            // Validar si el correo ya existe en otra cuenta
            if (existeEmail(cuenta.email(), cuenta.id())) {
                throw new Exception("El correo: " + cuenta.email() + " ya está registrado en otra cuenta.");
            }

            // Validar el estado de la cuenta
            if (cuentaModificada.getEstado().equals(EstadoCuenta.INACTIVO)) {
                throw new Exception("La cuenta se encuentra inactiva");
            }
            if (cuentaModificada.getEstado().equals(EstadoCuenta.ELIMINADO)) {
                throw new Exception("La cuenta ha sido eliminada");
            }

            // Imprimir el ID original
            System.out.println("ID original: " + cuentaModificada.getId());

            // Se construye la cuenta actualizada solo con los campos modificados.
            Cuenta cuentaActualizada = cuentaModificada.toBuilder()

                    //.id(cuentaModificada.getId()) // Aseguramos que el ID original de la cuenta no se modifique
                    .email(cuenta.email() )
                    .usuario(cuentaModificada.getUsuario().toBuilder()
                            .cedula(cuenta.cedula() )
                            .direccion(cuenta.direccion())  // Siempre presente, no es null
                            .nombre(cuenta.nombre())  // Siempre presente, no es null
                            .telefonos(cuenta.telefonos())  // Siempre presente, no es null
                            .build())
                    .build();
            System.out.println("ID después del Builder: " + cuentaActualizada.getId());

            // Se construye la instancia "cuentaActualizada". Se guarda la cuenta actualizada en el repositorio.
            cuentaRepo.save(cuentaActualizada);

            System.out.println("Cuenta actualizada: " + cuentaActualizada);

            // Retornar el ID de la cuenta actualizada
            return cuentaActualizada.getId();

        } catch (Exception e) {

            // Manejar cualquier excepción que ocurra
            e.printStackTrace(); // Registrar el error para depuración
            throw new RuntimeException("Error al actualizar la cuenta: " + e.getMessage(), e);
        }
    }

    // Método sobrecargado para excluir la cuenta actual de la validación
    private boolean existeCedula(String cedula, String idCuentaActual) {
        return cuentaRepo.buscarCedula(cedula)
                .filter(cuenta -> !cuenta.getId().equals(idCuentaActual)) // Excluir la cuenta actual
                .isPresent();
    }

    // Método sobrecargado para excluir la cuenta actual de la validación del correo
    private boolean existeEmail(String email, String idCuentaActual) {
        return cuentaRepo.buscarEmail(email)
                .filter(cuenta -> !cuenta.getId().equals(idCuentaActual)) // Excluir la cuenta actual
                .isPresent();
    }

    @Override
    public String eliminarCuenta(String id) throws Exception {

        try {
            // Cuenta cuenta = obtenerCuentaPorId(id);
            // Paso 1: Buscar la cuenta en el repositorio usando su ID.
            Optional<Cuenta> cuentaOptional = cuentaRepo.buscarId(id);

            // Paso 2: Validar si la cuenta existe.
            if (cuentaOptional.isEmpty()) {
                throw new Exception("La cuenta no existe");
            }

            // Paso 3: Recuperar la cuenta desde el Optional
            // El método Optional.get() extrae el objeto Cuenta del Optional.
            Cuenta cuenta = cuentaOptional.get();

            // Paso 4: Validar sí la cuenta ya fue eliminada.
            if (cuenta.getEstado().equals(EstadoCuenta.ELIMINADO)) {
                throw new Exception("La cuenta ya ha sido eliminada:" + id);
            }

            // Aunque sabemos que el frontend no permite el acceso hasta aquí.
            // Paso 5: Validar si la cuenta está inactiva
            if (cuenta.getEstado().equals(EstadoCuenta.INACTIVO)) {
                // Si la cuenta ya está en estado ELIMINADO, lanzar una excepción.
                throw new Exception("La cuenta no ha sido activada:" + id);
            }

            // Paso 6: Cambiar el estado de la cuenta a ELIMINADO.
            cuenta.setEstado(EstadoCuenta.ELIMINADO);

            // Paso 7: Guardar la cuenta actualizada en el repositorio
            cuentaRepo.save(cuenta);

            // Paso 8: Retornar el ID de la cuenta eliminada como confirmación
            return id;

        } catch (Exception e) {

            // Manejar cualquier excepción que ocurra
            e.printStackTrace(); // Registrar el error para depuración
            throw new RuntimeException("Error al eliminar la cuenta: " + e.getMessage(), e);
        }
    }

    @Override
    public Cuenta obtenerCuenta(String id) throws Exception {

        // Buscar la cuenta en el repositorio por ID
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarId(id);

        // Si no se encuentra, lanzar una excepción
        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con el ID: " + id + " no existe.");
        }

        // Retornar la cuenta si existe
        return cuentaOptional.get();
    }

    @Override
    public InformacionCuentaDTO obtenerInformacionCuenta(String id) throws Exception {

        //Obtenemos la cuenta del usuario
        Cuenta cuenta = obtenerCuenta(id);

        // Validar si la cuenta está eliminada
        if (cuenta.getEstado().equals(EstadoCuenta.ELIMINADO)) {
            throw new Exception("La cuenta con el ID " + id + " está eliminada.");
        }

        // Se retorna la información de la cuenta del usuario
        // return new InformacionCuentaDTO(
        return InformacionCuentaDTO.builder()
                .id(cuenta.getId() )
                .cedula(cuenta.getUsuario().getCedula() )
                .direccion(cuenta.getUsuario().getDireccion() )
                .email(cuenta.getEmail() )
                .nombre(cuenta.getUsuario().getNombre() )
                .telefono(cuenta.getUsuario().getTelefonos() )
                .build();
    }
}
