package dev.andresm.unieventosMongodb.servicios.implement;

import dev.andresm.unieventosMongodb.documentos.Cupon;
import dev.andresm.unieventosMongodb.documentos.EstadoCupon;
import dev.andresm.unieventosMongodb.documentos.TipoCupon;
import dev.andresm.unieventosMongodb.dto.cupon.*;
import dev.andresm.unieventosMongodb.repositorios.CuentaRepo;
import dev.andresm.unieventosMongodb.repositorios.CuponRepo;
import dev.andresm.unieventosMongodb.servicios.interfaces.CuponServicio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * =================================================================================
 * 📌 IMPLEMENTACIÓN DEL SERVICIO DE CUPONES
 * =================================================================================

 * - Contiene la lógica de negocio relacionada con los cupones
 * - Valida reglas antes de persistir información
 * - Usa Optional para control seguro de null
 * - Usa Builder SOLO cuando se crea la entidad
 * - Mantiene consistencia con MongoDB y Spring Data
 */
@Service
@Transactional
public class CuponServicioImp implements CuponServicio {
    private final CuponRepo cuponRepo;;
    private final CuentaRepo cuentaRepo;


    public CuponServicioImp(CuponRepo cuponRepo, CuentaRepo cuentaRepo) {
        this.cuponRepo = cuponRepo;
        this.cuentaRepo = cuentaRepo;
    }

    /**
     * =================================================================================
     * 📌 CREAR CUPÓN
     * =================================================================================

     * - Valida fecha de vencimiento
     * - Verifica que el código no exista
     * - Usa Builder porque se crea la entidad desde cero
     */
    @Override
    public String crearCupon(CrearCuponDTO cuponDTO) throws Exception {

        /**
         * =================================================================================
         * 📌 VALIDACIÓN DE FECHA DE VENCIMIENTO
         * =================================================================================

         * - Un cupón no puede crearse con fecha en el pasado
         * - Se valida antes de cualquier operación en base de datos
         */
        if (cuponDTO.fechaVencimiento().isBefore(LocalDateTime.now()))
            throw new Exception("La fecha de vencimiento no puede ser en el pasado");

        /**
         * =================================================================================
         * 📌 VALIDACIÓN DE CÓDIGO DEL CUPÓN
         * =================================================================================

         * - Se consulta el repositorio para verificar si ya existe un cupón
         *   con el mismo código.

         * - buscarCodigo(...) retorna un Optional<Cupon>
         *     ✔ Optional vacío  → el código es válido
         *     ❌ Optional lleno  → el código ya existe → ERROR

         * - Esto evita duplicados y garantiza integridad del negocio
         */
        if (cuponRepo.buscarCodigo(cuponDTO.codigo()).isPresent()) {
            throw new Exception("El código del cupón ya existe");
        }

        /**
         * =================================================================================
         * 📌 DEFINICIÓN DEL CÓDIGO FINAL DEL CUPÓN
         * =================================================================================

         * Aquí se decide qué código tendrá el cupón:

         * - Si el cupón es UNICO:
         *     → Se usa el código enviado en el DTO

         * - Si el cupón es INDIVIDUAL:
         *     → Se genera un código automático

         * Esta decisión se toma una sola vez para:
         * ✔ evitar repetir if
         * ✔ simplificar el Builder
         * ✔ dejar la lógica clara y centralizada
         */
        String codigoFInal = cuponDTO.tipo() == TipoCupon.UNICO
                ? cuponDTO.codigo()
                : generarCodigoIndividual();

        /**
         * =================================================================================
         * 📌 DEFINICIÓN DE BENEFICIARIOS DEL CUPÓN
         * =================================================================================

         * - Este bloque se encarga de inicializar correctamente la lista de beneficiarios
         *   del cupón antes de construir la entidad.

         * - Si el cupón es de tipo INDIVIDUAL:
         *   → Se asigna la lista de beneficiarios enviada en el DTO.

         * - Si el cupón es de tipo UNICO:
         *   → Se inicializa una lista vacía.

         * - Esto garantiza que el atributo "beneficiarios" nunca sea null,
         *   evitando NullPointerException en operaciones posteriores como:
         *     ✔ redención de cupones
         *     ✔ validaciones de cliente
         *     ✔ persistencia en la base de datos

         * - De esta forma la entidad Cupon siempre queda en un estado consistente
         *   desde el momento de su creación.
         * =================================================================================
         */
        List<String> beneficiarios = cuponDTO.tipo() == TipoCupon.INDIVIDUAL
                ? cuponDTO.beneficiarios()
                : new ArrayList<>();

        Cupon cupon = Cupon.builder()
                .codigo(codigoFInal)
                .nombre(cuponDTO.nombre())
                .descuento(cuponDTO.descuento())
                .estado(EstadoCupon.DISPONIBLE)
                .tipo(cuponDTO.tipo())
                .fechaVencimiento(cuponDTO.fechaVencimiento())
                .beneficiarios(beneficiarios)
                .build();

        cuponRepo.save(cupon);
        return cupon.getCodigo();
    }

    /**
     * =================================================================================
     * 📌 MÉTODO AUXILIAR
     * =================================================================================

     * Genera un código único para cupones individuales.
     */
    private String generarCodigoIndividual() {
        return "CUPON-" + UUID.randomUUID();
    }

    /**
     * =================================================================================
     * 📌 ACTUALIZAR CUPÓN
     * =================================================================================

     * - Usa Optional como en el ejemplo base
     * - NO usa Builder (es una actualización)
     */
    @Override
    public String actualizarCupon(ActualizarCuponDTO cuponDTO) throws Exception {
        Optional<Cupon> cuponOptional = cuponRepo.buscarId(cuponDTO.id());

        if (cuponOptional.isEmpty()) {
            throw new Exception("El cupón no existe");
        }

        if (cuponDTO.fechaVencimiento().isBefore(LocalDateTime.now())) {
            throw new Exception("La fecha de vencimiento no puede ser en el pasado");
        }

        Cupon cupon = cuponOptional.get();
        cupon.setNombre(cuponDTO.nombre());
        cupon.setDescuento(cuponDTO.descuento());
        cupon.setEstado(cuponDTO.estadoCupon());
        cupon.setBeneficiarios(cuponDTO.beneficiarios());
        cupon.setFechaVencimiento(cuponDTO.fechaVencimiento());

        cuponRepo.save(cupon);
        return cupon.getId();
    }

    /**
     * =================================================================================
     * 📌 BORRAR CUPÓN (BORRADO LÓGICO)
     * =================================================================================

     * - Cambia el estado a NO_DISPONIBLE
     */
    @Override
    public void borrarCupon(String idCupon) throws Exception {
        Optional<Cupon> cuponOptional = cuponRepo.buscarId(idCupon);

        if (cuponOptional.isEmpty()) {
            throw new Exception("El cupón no existe");
        }

        Cupon cupon = cuponOptional.get();
        cupon.setEstado(EstadoCupon.NO_DISPONIBLE);
        cuponRepo.save(cupon);
    }

    /**
     * =================================================================================
     * 📌 REDIMIR CUPÓN
     * =================================================================================

     * Permite redimir un cupón para un cliente específico.

     * Flujo del proceso:
     * - Se busca el cupón por su código
     * - Se valida que el cupón exista
     * - Se valida que el cupón esté disponible y no vencido
     * - Se inicializa la lista de beneficiarios si es null
     * - Se aplica la lógica según el tipo de cupón:

     *   • CUPÓN UNICO:
     *       - Se asigna al cliente
     *       - Se marca como NO DISPONIBLE

     *   • CUPÓN INDIVIDUAL:
     *       - Se valida que el cliente esté autorizado
     *       - Se elimina al cliente de la lista de beneficiarios

     * - Se guarda el estado final del cupón
     */
    @Override
    public boolean redimirCupon(RedimirCuponDTO redimirCuponDTO) throws Exception {

        /**
         * =================================================================================
         * 📌 BÚSQUEDA DEL CUPÓN
         * =================================================================================

         * Se consulta el repositorio utilizando el código del cupón.
         * - Si no existe un cupón con ese código → no se puede redimir.

         * Se usa Optional para evitar NullPointerException
         * y manejar correctamente el caso de ausencia.
         */
        Optional<Cupon> cuponOptional = cuponRepo.buscarCodigo(redimirCuponDTO.codigoCupon());

        if (cuponOptional.isEmpty()) {
            throw new Exception("El cupón no existe");
        }
        /**
         * =================================================================================
         * 📌 VERIFICACIÓN DE DISPONIBILIDAD DEL CUPÓN
         * =================================================================================

         * Se valida que el cupón:
         * - Esté en estado DISPONIBLE
         * - No esté vencido

         * Si no cumple estas condiciones, no se permite la redención.
         */
        if (!verificarDisponibilidadCupon(redimirCuponDTO.codigoCupon())) {
            throw new Exception("El cupón ya no se encuentra disponible");
        }
        Cupon cupon = cuponOptional.get();

        /**
         * =================================================================================
         * 📌 INICIALIZACIÓN DE BENEFICIARIOS
         * =================================================================================

         * Si la lista de beneficiarios es null,
         * se inicializa para evitar errores en operaciones posteriores.
         */
        if (cupon.getBeneficiarios() == null) {
            cupon.setBeneficiarios(new ArrayList<>());
        }

        /**
         * =================================================================================
         * 📌 REDENCIÓN SEGÚN TIPO DE CUPÓN
         * =================================================================================

         * - CUPÓN UNICO:
         *     • Se registra el cliente como beneficiario
         *     • El cupón pasa a estado NO_DISPONIBLE

         * - CUPÓN INDIVIDUAL:
         *     • Se valida que el cliente esté autorizado
         *     • Se elimina al cliente de la lista de beneficiarios
         */

        /**
         * =================================================================================
         * 📌 VALIDACIÓN DEL TIPO DE CUPÓN (MANEJO DE NULL)
         * =================================================================================

         * IMPORTANTE:
         * El atributo "tipo" del cupón podría ser null (por errores de carga,
         * datos incompletos o registros inconsistentes en la base de datos).

         * ❌ Forma peligrosa (puede lanzar NullPointerException):
         *     if (cupon.getTipo() == TipoCupon.UNICO) { }

         * ✅ Forma segura:
         *     TipoCupon.UNICO.equals(cupon.getTipo())

         * Esto se debe a que equals() se invoca sobre un valor NO NULL
         * (el enum), por lo tanto, si cupon.getTipo() es null,
         * simplemente retorna false y el sistema no falla.
         */
        if (TipoCupon.UNICO.equals(cupon.getTipo())) {
            /**
             * -----------------------------------------------------------------------------
             * 🎟 CUPÓN ÚNICO
             * -----------------------------------------------------------------------------

             * - Se registra el cliente como beneficiario
             * - El cupón pasa a estado NO_DISPONIBLE, ya que solo puede
             *   ser redimido una vez.
             */
            cupon.getBeneficiarios().add(redimirCuponDTO.idCliente());
            cupon.setEstado(EstadoCupon.NO_DISPONIBLE);
            cuponRepo.save(cupon);
        } else if (TipoCupon.INDIVIDUAL.equals(cupon.getTipo())) {
            /**
             * -----------------------------------------------------------------------------
             * 🎟 CUPÓN INDIVIDUAL
             * -----------------------------------------------------------------------------

             * - Se valida que el cliente esté autorizado para redimir el cupón
             * - Si no está en la lista de beneficiarios, se lanza una excepción
             */
            if (!cupon.getBeneficiarios().contains(redimirCuponDTO.idCliente())) {
                throw new IllegalArgumentException("El cliente no cuenta con este cupón");
            }

            cupon.getBeneficiarios().remove(redimirCuponDTO.idCliente());
            cuponRepo.save(cupon);
        } else {
            /**
             * -----------------------------------------------------------------------------
             * ⚠ TIPO DE CUPÓN NULO O INVÁLIDO
             * -----------------------------------------------------------------------------

             * Si el tipo del cupón es null o no corresponde a ningún valor
             * válido del enum, el sistema no puede continuar con la redención.
             */
            throw new Exception("El tipo de cupón no es válido");
        }

        return true;
    }

    /**
     * =================================================================================
     * 📌 VERIFICAR DISPONIBILIDAD DE UN CUPÓN
     * =================================================================================

     * Este método se encarga de validar si un cupón puede ser utilizado.

     * Un cupón se considera DISPONIBLE cuando:
     * ✔ Existe en la base de datos
     * ✔ Su estado es DISPONIBLE
     * ✔ La fecha de vencimiento NO ha pasado

     * Flujo del método:

     * 1️⃣ Se busca el cupón por su código en el repositorio
     *     - Si NO existe → retorna false

     * 2️⃣ Si existe, se valida:
     *     - Estado == DISPONIBLE
     *     - Fecha de vencimiento > fecha actual

     * 3️⃣ Si ambas condiciones se cumplen → retorna true
     *    En cualquier otro caso → retorna false
     *
     * @param codigoCupon Código del cupón a validar
     * @return true si el cupón está disponible, false en caso contrario
     */
    public boolean verificarDisponibilidadCupon(String codigoCupon) {
        Optional<Cupon> cuponOptional = cuponRepo.buscarCodigo(codigoCupon);

        // Si el cupón no existe, no está disponible
        if (cuponOptional.isEmpty()) {
            return false;
        }

        Cupon cupon = cuponOptional.get();

        // Se valida estado y fecha de vencimiento
        if (cupon.getEstado() == EstadoCupon.DISPONIBLE && cupon.getFechaVencimiento().isAfter(LocalDateTime.now())) {
            return true;
        }
        return false;
    }


    /**
     *
     * =================================================================================
     * 📌 MÉTODO AUXILIAR
     * =================================================================================

     * =================================================================================
     * 📌 VALIDACIÓN DE EXISTENCIA DE CÓDIGO DE CUPÓN
     * =================================================================================

     * Método auxiliar que verifica si ya existe un cupón
     * registrado con el código proporcionado.

     * Se utiliza para:
     * ✔ evitar duplicidad de códigos
     * ✔ centralizar la lógica de validación
     *
     * @param codigo Código del cupón
     * @return true si el código ya existe, false si es único
     */
    private  boolean existeCodigo(String codigo) {
        return cuponRepo.buscarCodigo(codigo).isPresent();
    }

    /**
     *
     * =================================================================================
     * 📌 MÉTODO AUXILIAR
     * =================================================================================

     * 📌 VALIDACIÓN DE EXISTENCIA DE NOMBRE DE CUPÓN
     * =================================================================================

     * Método auxiliar que valida si ya existe un cupón
     * con el mismo nombre en el sistema.

     * Se usa para:
     * ✔ evitar duplicidad de nombres
     * ✔ mantener integridad de datos
     *
     * @param nombre Nombre del cupón
     * @return true si el nombre ya existe, false en caso contrario
     */
    private boolean existeNombre(String nombre) {
        return cuponRepo.buscarNombre(nombre).isPresent();
    }

    /**
     * =================================================================================
     * 📌 LISTAR CUPONES DISPONIBLES
     * =================================================================================

     * Este método obtiene todos los cupones que pueden ser utilizados.

     * Reglas aplicadas:
     * ✔ El cupón debe estar en estado DISPONIBLE
     * ✔ La fecha de vencimiento debe ser posterior a la fecha actual

     * Flujo del método:
     * 1️⃣ Se consultan todos los cupones almacenados
     * 2️⃣ Se filtran únicamente los cupones válidos
     * 3️⃣ Cada entidad Cupon se transforma en un ItemCuponDTO
     *
     * @return Lista de cupones disponibles en formato ItemCuponDTO
     */
   @Override
    public List<ItemCuponDTO> listarCupones() {
       return cuponRepo.findAll().stream().filter(cupon -> cupon.getEstado() == EstadoCupon.DISPONIBLE
               && cupon.getFechaVencimiento().isAfter(LocalDateTime.now()))
               .map(cupon -> new ItemCuponDTO(
                       cupon.getId(),
                       cupon.getCodigo(),
                       cupon.getNombre(),
                       cupon.getDescuento(),
                       cupon.getFechaVencimiento(),
                       cupon.getTipo()
               ))
               .collect(Collectors.toList());
    }

    /**
     * =================================================================================
     * 📌 LISTAR CUPONES DISPONIBLES PARA UN CLIENTE
     * =================================================================================

     * Este método retorna los cupones que un cliente puede usar.

     * Reglas aplicadas:
     * ✔ El cupón debe estar DISPONIBLE
     * ✔ El cupón no debe estar vencido

     * Condiciones por tipo:
     * - UNICO:
     *     → Está disponible para cualquier cliente

     * - INDIVIDUAL:
     *     → El cliente debe estar incluido en la lista de beneficiarios

     * Flujo del método:
     * 1️⃣ Se consultan todos los cupones
     * 2️⃣ Se filtran los cupones válidos según reglas de negocio
     * 3️⃣ Se transforman en ItemCuponDTO
     *
     * @param listarCuponDTO DTO que contiene el id del cliente
     * @return Lista de cupones disponibles para el cliente
     */
    @Override
    public List<ItemCuponDTO> listarCuponesCliente(ListarCuponDTO listarCuponDTO) {
        return  cuponRepo.findAll().stream().filter(cupon -> cupon.getEstado() == EstadoCupon.DISPONIBLE
                && cupon.getFechaVencimiento().isAfter(LocalDateTime.now())
                && (cupon.getTipo() == TipoCupon.UNICO
                || (cupon.getTipo() == TipoCupon.INDIVIDUAL
                && cupon.getBeneficiarios().contains(listarCuponDTO.idCliente()))))
                .map(cupon -> new ItemCuponDTO(
                        cupon.getId(),
                        cupon.getCodigo(),
                        cupon.getNombre(),
                        cupon.getDescuento(),
                        cupon.getFechaVencimiento(),
                        cupon.getTipo()
                ))
                .collect(Collectors.toList());
    }
}
