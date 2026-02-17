package dev.andresm.unieventosMongodb.servicios.implement;

import dev.andresm.unieventosMongodb.config.JWTUtils;
import dev.andresm.unieventosMongodb.documentos.Cuenta;
import dev.andresm.unieventosMongodb.dto.cuenta.*;
import dev.andresm.unieventosMongodb.dto.cupon.CrearCuponDTO;
import dev.andresm.unieventosMongodb.dto.email.EmailDTO;
import dev.andresm.unieventosMongodb.dto.cuenta.CodigoPasswordDTO;
import dev.andresm.unieventosMongodb.documentos.*;
import dev.andresm.unieventosMongodb.repositorios.CuentaRepo;
import dev.andresm.unieventosMongodb.repositorios.EventoRepo;
import dev.andresm.unieventosMongodb.servicios.interfaces.CuentaServicio;
import dev.andresm.unieventosMongodb.servicios.interfaces.CuponServicio;
import dev.andresm.unieventosMongodb.servicios.interfaces.EmailServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio encargado de la lógica de negocio relacionada con las cuentas.
 * Gestiona:
 * - Registro y activación de cuentas
 * - Autenticación y generación de tokens
 * - Actualización y eliminación de cuentas
 * - Recuperación de contraseña
 * - Gestión del carrito de eventos
 */
@Service
@Transactional
@RequiredArgsConstructor

public class CuentaServicioImp implements CuentaServicio {

    private final CuentaRepo cuentaRepo;
    private final EmailServicio emailServicio;
    private final JWTUtils jwtUtils;
    private final CuponServicio cuponServicio;
    private final EventoRepo eventoRepo;

    /**
     * Crea una nueva cuenta de usuario.
     * Valida que el correo y la cédula no estén registrados,
     * encripta la contraseña y envía un código de activación
     * por correo electrónico.
     *
     * @param cuenta datos necesarios para crear la cuenta
     * @return id de la cuenta creada
     * @throws Exception si el correo o la cédula ya existen
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
                            .cedula(cuenta.cedula())
                            .direccion(cuenta.direccion())
                            .nombre(cuenta.nombre())
                            .telefonos(cuenta.telefonos())
                            .build()
                    )
                    .build();

            // Enviar correo de activación
            // EmailDTO emailDTO = new EmailDTO(
            EmailDTO emailDTO = EmailDTO.builder()

                    .asunto("Su código de activación es: " + codigoValidacionRegistro.getCodigo())
                    .contenido("Ingrese el código para poder activar su cuenta")
                    .destinatario(cuenta.email())
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

    /**
     * 🔹 Encripta una contraseña en texto plano.
     * Utiliza BCrypt para garantizar que la contraseña no se almacene en texto claro.
     *
     * @param password Contraseña ingresada por el usuario.
     * @return Contraseña encriptada.
     */
    private String encriptarPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 🔹 Genera un código aleatorio alfanumérico.
     * Se utiliza para activación de cuenta y recuperación de contraseña.
     *
     * @return Código de verificación generado.
     */
    @Override
    public String generarCodigo() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int tam = 10;
        SecureRandom random = new SecureRandom();
        StringBuilder codigo = new StringBuilder(tam);

        for (int i = 0; i < tam; i++) {
            int index = random.nextInt(caracteres.length());
            codigo.append(caracteres.charAt(index));
        }
        return codigo.toString();
    }

    /**
     * Activa una cuenta usando un código de verificación.
     * Valida el código y su tiempo de expiración.
     * Si el código expira, se genera uno nuevo y se envía por correo.
     *
     * @param activarCuentaDTO email y código de verificación
     * @return true si la cuenta fue activada correctamente
     * @throws Exception si la cuenta no existe o el código es inválido
     */
    @Override
    public boolean activarCuenta(ActivarCuentaDTO activarCuentaDTO) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarEmail(activarCuentaDTO.email());

        if (cuentaOptional.isEmpty()) {
            throw new Exception("El cuenta no existe");
        }
        Cuenta cuenta = cuentaOptional.get();

        if (!activarCuentaDTO.codigoVerificacion().equals(cuenta.getCodigoValidacionRegistro().getCodigo())) {
            throw new Exception("Código de verificación incorrecto");
        }

        LocalDateTime fechaCreacion = cuenta.getCodigoValidacionRegistro().getFechaCreacion();
        LocalDateTime fechaActual = LocalDateTime.now();
        Duration duracionValidez = Duration.ofMinutes(15);

        if (Duration.between(fechaCreacion, fechaActual).compareTo(duracionValidez) > 0) {
            cuenta.setCodigoValidacionRegistro(new CodigoValidacion(generarCodigo(), LocalDateTime.now()));

            EmailDTO emailDTO = EmailDTO.builder()
                    .asunto("Su código de activación es: " + cuenta.getCodigoValidacionRegistro().getCodigo())
                    .contenido("Ingrese el código para poder activar su cuenta")
                    .destinatario(cuenta.getEmail())
                    .build();

            emailServicio.enviarEmail(emailDTO);
            cuentaRepo.save(cuenta);
            throw new Exception("El código de verificación ha expirado");
        }
        List<String> b = new ArrayList<>();
        b.add(cuenta.getId());
        String codigoCupon = cuponServicio.crearCupon(new CrearCuponDTO("Cupon R-1", "Código de bienvenida", 15, LocalDateTime.now().plusYears(2), TipoCupon.INDIVIDUAL, b));
        emailServicio.enviarEmail(new EmailDTO("¡Te damos la bienvenida! Disfruta un 15% de descuento en Unieventos", "Esté es tu código:" + codigoCupon, cuenta.getEmail()));
        cuenta.setEstado(EstadoCuenta.ACTIVO);
        cuenta.setCodigoValidacionRegistro(null);

        cuentaRepo.save(cuenta);
        return true;
    }


    /**
     * Obtiene una lista básica de todas las cuentas registradas.
     * Retorna información resumida sin datos sensibles.
     *
     * @return lista de cuentas en formato ItemCuentaDTO
     */
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

    /**
     * Actualiza los datos personales de una cuenta existente.
     * Valida que el correo y la cédula no estén registrados
     * en otra cuenta y que la cuenta esté activa.
     *
     * @param cuenta datos actualizados de la cuenta
     * @return id de la cuenta actualizada
     * @throws Exception si la cuenta no existe o está inactiva
     */
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
                    .email(cuenta.email())
                    .usuario(cuentaModificada.getUsuario().toBuilder()
                            .cedula(cuenta.cedula())
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
        return cuentaRepo.buscarCedulaIdDiferente(cedula, idCuentaActual)
                .isPresent();
    }

    // Método sobrecargado para excluir la cuenta actual de la validación del correo
    private boolean existeEmail(String email, String idCuentaActual) {
        return cuentaRepo.buscarEmailIdDiferente(email,idCuentaActual)
                .isPresent();
    }

    /**
     * Elimina lógicamente una cuenta.
     * Cambia el estado de la cuenta a ELIMINADO,
     * sin borrar el documento de la base de datos.
     *
     * @param id identificador de la cuenta
     * @return id de la cuenta eliminada
     * @throws Exception si la cuenta no existe o ya fue eliminada
     */
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

    /**
     * 🔹 Obtiene una cuenta por su identificador.
     *
     * @param id Identificador único de la cuenta.
     * @return Cuenta encontrada.
     * @throws Exception Si la cuenta no existe.
     */
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

    /**
     * 🔹 Obtiene una cuenta a partir del correo electrónico.
     * Valida que la cuenta exista y que no esté inactiva o eliminada.
     *
     * @param email Correo electrónico del usuario.
     * @return Cuenta válida.
     * @throws Exception Si la cuenta no existe o su estado no es válido.
     */
    @Override
    public Cuenta obtenerEmail(String email) throws Exception {

        // Buscar la cuenta en el repositorio por email
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarEmail(email);

        // Si no se encuentra, lanzar una excepción
        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta con el email: " + email + " no existe.");
        }

        // Obtener la cuenta
        Cuenta cuenta = cuentaOptional.get();

        // Validar el estado de la cuenta
        if (cuenta.getEstado().equals(EstadoCuenta.ELIMINADO)) {
            throw new Exception("La cuenta con el email: " + email + " ha sido eliminada.");
        }
        if (cuenta.getEstado().equals(EstadoCuenta.INACTIVO)) {
            throw new Exception("La cuenta con el email: " + email + " está inactiva.");
        }

        // Retornar la cuenta válida
        return cuenta;
    }

    /**
     * 🔹 Retorna la información básica de una cuenta.
     * Se usa para mostrar datos del perfil del usuario.
     *
     * @param id Identificador de la cuenta.
     * @return Información de la cuenta.
     * @throws Exception Si la cuenta no existe o está eliminada.
     */
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
                .id(cuenta.getId())
                .cedula(cuenta.getUsuario().getCedula())
                .direccion(cuenta.getUsuario().getDireccion())
                .email(cuenta.getEmail())
                .nombre(cuenta.getUsuario().getNombre())
                .telefono(cuenta.getUsuario().getTelefonos())
                .build();
    }

    /**
     * 🔹 Envía un código de recuperación de contraseña al correo del usuario.
     * El código tiene una vigencia limitada.
     *
     * @param codigoPasswordDTO Contiene el correo del usuario.
     * @return Mensaje de confirmación.
     * @throws Exception Si la cuenta no existe o no es válida.
     */
    @Override
    public String enviarCodigoRecuperacionPassword(CodigoPasswordDTO codigoPasswordDTO) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarEmail(codigoPasswordDTO.email());

        if (cuentaOptional.isEmpty()) {
            throw new Exception("El correo no se encuentra registrado");
        }
        Cuenta cuenta = cuentaOptional.get();

        if (cuenta.getEstado().equals(EstadoCuenta.ELIMINADO)) {
            throw new Exception("La cuenta ha sido eliminada");
        }

        if (cuenta.getEstado().equals(EstadoCuenta.INACTIVO)) { //Aunque sabemos que el frontend no permite el acceso hasta aquí.
            throw new Exception("La cuenta no ha sido activada");
        }
        String codigoValidacion = generarCodigo();

        // Se asigna el codigo de recuepración.
        cuenta.setCodigoValidacionPassword(new CodigoValidacion(codigoValidacion, LocalDateTime.now()));

        // @Builder del EmailDTO
        EmailDTO email = EmailDTO.builder()
                .asunto("Código de recuperación de contraseña")
                .contenido("Ingrese solo el código: " + codigoValidacion)
                .destinatario(codigoPasswordDTO.email())
                .build();

        emailServicio.enviarEmail(email);
        cuentaRepo.save(cuenta);

        return "Se ha enviado un correo con el código de recuperación";

    }

    /**
     * 🔹 Cambia la contraseña de una cuenta usando un código de recuperación.
     * Valida el código y su tiempo de expiración.
     *
     * @param cambiarPasswordDTO Contiene email, código y nueva contraseña.
     * @return Mensaje de confirmación.
     * @throws Exception Si el código es inválido, expiró o la cuenta no es válida.
     */
    @Override
    public String cambiarPassword(CambiarPasswordDTO cambiarPasswordDTO) throws Exception {
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarEmail(cambiarPasswordDTO.email());

        if (cuentaOptional.isEmpty()) {
            throw new Exception("El correo dado no está registrado");
        }
        Cuenta cuenta = cuentaOptional.get();

        if (cuenta.getEstado().equals(EstadoCuenta.INACTIVO)) {
            throw new Exception("La cuenta no se encuentra activa");
        }
        if (cuenta.getEstado().equals(EstadoCuenta.ELIMINADO)) {
            throw new Exception("La cuenta ha sido eliminada");
        }
        CodigoValidacion codigoValidacion = cuenta.getCodigoValidacionPassword();

        if (codigoValidacion == null) {
            throw new Exception("No hay un código de recuperación activo");
        }
        if (codigoValidacion.getCodigo().equals(cambiarPasswordDTO.codigoVerificacion())) {
            LocalDateTime fechaCreacion = codigoValidacion.getFechaCreacion();

            if (Duration.between(fechaCreacion, LocalDateTime.now()).toMinutes() <= 15) {
                String passwordEncriptada = encriptarPassword(cambiarPasswordDTO.passwordNuevo());
                cuenta.setPassword(passwordEncriptada);

                cuenta.setCodigoValidacionPassword(null);
                cuentaRepo.save(cuenta);

                return "Contraseña cambiada correctamente";
            } else {
                throw new Exception("El código de validación ya expiró");
            }
        } else {
            throw new Exception("El código de validación es incorrecto");
        }
    }

    /**
     * Inicia sesión de un usuario.
     * Valida las credenciales y genera un token JWT
     * con la información básica del usuario.
     *
     * @param loginDTO email y contraseña
     * @return token JWT
     * @throws Exception si las credenciales son inválidas
     */
    @Override
    public TokenDTO iniciarSesion(LoginDTO loginDTO) throws Exception {
        // Obtener y validar la cuenta
        Cuenta cuenta = obtenerEmail(loginDTO.email());

        // Validar la contraseña
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (!passwordEncoder.matches(loginDTO.password(), cuenta.getPassword())) {
            throw new Exception("La contraseña es incorrecta");
        }

        // Construir los claims y generar el token
        Map<String, Object> map = construirClaims(cuenta);
        return new TokenDTO(jwtUtils.generarToken(cuenta.getEmail(), map));
    }

    private Map<String, Object> construirClaims(Cuenta cuenta) {
        return Map.of(
                "rol", cuenta.getRol(),
                "nombre", cuenta.getUsuario().getNombre(),
                "id", cuenta.getId()
        );
    }

    /**
     * Agrega un evento al carrito del usuario.
     * Valida la existencia del evento, la localidad
     * y la disponibilidad de entradas.
     *
     * @param agregarEventoDTO información del evento a agregar
     * @return mensaje de confirmación
     * @throws Exception si el evento o la cuenta no son válidos
     */
    @Override
    public String agregarEventoCarrito(AgregarEventoDTO agregarEventoDTO) throws Exception {

        // 1. Buscar cuenta
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarId(agregarEventoDTO.idUsuario());

        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta no existe");
        }

        Cuenta cuenta = cuentaOptional.get();

        if (!cuenta.getRol().equals(Rol.CLIENTE)) {
            throw new Exception("El usuario no tiene carrito");
        }

        if (cuenta.getEstado().equals(EstadoCuenta.ELIMINADO)) {
            throw new Exception("La cuenta ha sido eliminada");
        }

        // 2. Buscar evento
        Optional<Evento> optionalEvento = eventoRepo.buscarId(agregarEventoDTO.idEvento());

        if (optionalEvento.isEmpty()) {
            throw new Exception("El evento no fue encontrado");
        }

        Evento evento = optionalEvento.get();

        // 3. Buscar localidad
        Optional<Localidad> optionalLocalidad = evento.getLocalidades().stream()
                .filter(l -> l.getNombre().equals(agregarEventoDTO.nombreLocalidad()))
                .findFirst();

        if (optionalLocalidad.isEmpty()) {
            throw new Exception("La localidad no existe");
        }

        Localidad localidad = optionalLocalidad.get();

        if (localidad.cantidadDisponible() < agregarEventoDTO.cantidad()) {
            throw new Exception("No hay suficientes entradas disponibles, hay " + localidad.cantidadDisponible());
        }

        // 4. Obtener o crear carrito
        Carrito carrito = cuenta.getCarrito();

        if (carrito == null) {
            carrito = new Carrito();
            cuenta.setCarrito(carrito);
        }

        // 5. Crear detalle con Builder
        DetalleCarrito detalleCarrito = DetalleCarrito.builder()
                .idEvento(agregarEventoDTO.idEvento())
                .cantidad(agregarEventoDTO.cantidad())
                .nombreLocalidad(agregarEventoDTO.nombreLocalidad())
                .build();

        // 6. Agregar al carrito
        carrito.getItems().add(detalleCarrito);

        // 7. Guardar cuenta
        cuentaRepo.save(cuenta);

        return "Evento agregado al carrito con éxito";
    }

    /**
     * Edita un evento existente dentro del carrito.
     * Permite cambiar la localidad y la cantidad
     * validando disponibilidad.
     *
     * @param editarEventoCarritoDTO datos de edición
     * @return mensaje de confirmación
     * @throws Exception si el evento no existe en el carrito
     */
    @Override
    public String editarEventoCarrito(EditarEventoCarritoDTO editarEventoCarritoDTO) throws Exception {

        // 1. Buscar la cuenta del cliente por su ID
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarId(editarEventoCarritoDTO.idCliente());

        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta no existe");
        }

        Cuenta cuenta = cuentaOptional.get();

        // 2. Validaciones de rol y estado (Validaciones de negocio)
        if (!cuenta.getRol().equals(Rol.CLIENTE)) {
            throw new Exception("El usuario no tiene un carrito asignado");
        }

        if (cuenta.getEstado().equals(EstadoCuenta.ELIMINADO)) {
            throw new Exception("La cuenta ha sido eliminada");
        }

        if (cuenta.getEstado().equals(EstadoCuenta.INACTIVO)) {
            throw new Exception("La cuenta está inactiva");
        }

        // 3. Obtener el carrito
        Carrito carrito = cuenta.getCarrito();
        if (carrito == null || carrito.getItems().isEmpty()) {
            return "El carrito está vacío";
        }

        // 4. Buscar el detalle del carrito por su código
        Optional<DetalleCarrito> detalleOptional = carrito.getItems().stream().filter(detalle -> detalle.getCodigoDetalle().equals(editarEventoCarritoDTO.idDetalle())).findFirst();

        if (detalleOptional.isEmpty()) {
            throw new Exception("El evento no está en el carrito");
        }

        DetalleCarrito detalleCarrito = detalleOptional.get();

        // 5. Buscar el evento asociado al detalle
        Optional<Evento> eventoOptional = eventoRepo.buscarId(detalleCarrito.getIdEvento());

        if (eventoOptional.isEmpty()) {
            throw new Exception("El evento no fue encontrado");
        }

        Evento evento = eventoOptional.get();

        // 6. Buscar la nueva localidad
        Optional<Localidad> localidadOptional = evento.getLocalidades().stream().filter(l -> l.getNombre().equals(editarEventoCarritoDTO.nuevaLocalidad())).findFirst();

        if (localidadOptional.isEmpty()) {
            throw new Exception("La nueva localidad no existe");
        }

        Localidad nuevaLocalidad = localidadOptional.get();

        // 7. Validar disponibilidad
        if (nuevaLocalidad.cantidadDisponible() < editarEventoCarritoDTO.nuevaCantidad()) {
            throw new Exception( "No hay suficientes entradas disponibles, hay " + nuevaLocalidad.cantidadDisponible());
        }

        // 8. Actualizar el detalle del carrito
        detalleCarrito.setNombreLocalidad(editarEventoCarritoDTO.nuevaLocalidad());
        detalleCarrito.setCantidad(editarEventoCarritoDTO.nuevaCantidad());

        // 9. Guardar cambios
        cuentaRepo.save(cuenta);

        return "Evento del carrito editado con éxito";
    }

    /**
     * Elimina un evento del carrito del usuario.
     *
     * @param eliminarEventoDTO datos del evento a eliminar
     * @return mensaje de confirmación
     * @throws Exception si el evento no está en el carrito
     */
    @Override
    public String eliminarEventoCarrito(EliminarEventoDTO eliminarEventoDTO) throws Exception {

        // 1. Buscar la cuenta del cliente por su ID
        Optional<Cuenta> cuentaOptional = cuentaRepo.buscarId(eliminarEventoDTO.idCliente());

        if (cuentaOptional.isEmpty()) {
            throw new Exception("La cuenta no existe");
        }

        Cuenta cuenta = cuentaOptional.get();

        if (!cuenta.getRol().equals(Rol.CLIENTE)) {
            throw new Exception("El usuario no tiene carrito");
        }

        if (cuenta.getEstado().equals(EstadoCuenta.ELIMINADO)) {
            throw new Exception("La cuenta ha sido eliminada");
        }

        if (cuenta.getEstado().equals(EstadoCuenta.INACTIVO)) {
            throw new Exception("La cuenta está inactiva");
        }

        // 2. Obtener carrito
        Carrito carrito = cuenta.getCarrito();
        if (carrito == null || carrito.getItems().isEmpty()) {
            return "El carrito está vacío";
        }

        // 3. Buscar detalle por código
        Optional<DetalleCarrito> detalleOptional = carrito.getItems().stream().filter(detalle -> detalle.getCodigoDetalle().equals(eliminarEventoDTO.idDetalle()))
                .findFirst();

        if (detalleOptional.isEmpty()) {
            throw new Exception("El evento no está en el carrito");
        }

        // 4. Eliminar detalle
        carrito.getItems().remove(detalleOptional.get());

        // 5. Guardar cambios
        cuentaRepo.save(cuenta);

        return "Evento eliminado del carrito correctamente";
    }
}
