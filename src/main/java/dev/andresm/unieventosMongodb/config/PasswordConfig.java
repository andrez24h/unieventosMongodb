package dev.andresm.unieventosMongodb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Clase de configuración encargada de definir Beans relacionados
 * con la seguridad de la aplicación.

 * En este caso se registra un Bean de BCryptPasswordEncoder que será
 * utilizado para:

 * - Encriptar contraseñas antes de guardarlas en la base de datos
 * - Verificar contraseñas durante el proceso de autenticación

 * Al declararlo como Bean, Spring crea una única instancia del encoder
 * y la inyecta automáticamente en los servicios que lo necesiten.
 */
@Configuration
public class PasswordConfig {

    /**
     * Crea un Bean de BCryptPasswordEncoder.

     * Este objeto será administrado por Spring y podrá ser inyectado
     * en cualquier clase mediante inyección de dependencias.

     * @return instancia de BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        // 1. Crear el codificador de contraseñas utilizando el algoritmo BCrypt
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // 2. Retornar el encoder para que Spring lo registre como Bean
        return encoder;
    }
}
/**


@Bean (version 2)
public BCryptPasswordEncoder passwordEncoder(){
return new BCryptPasswordEncoder();
}
 *
 */