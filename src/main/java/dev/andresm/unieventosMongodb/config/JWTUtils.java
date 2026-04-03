package dev.andresm.unieventosMongodb.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

/**
 * Utilidad para la gestión de tokens JWT.

 * Esta clase se encarga de:
 * - Generar tokens JWT cuando un usuario se autentica correctamente.
 * - Validar y decodificar tokens enviados por el cliente en cada petición.

 * El token generado contiene información del usuario (claims),
 * el sujeto del token (email) y una fecha de expiración.
 */
@Component
public class JWTUtils {

    /**
     * Genera un token JWT para un usuario autenticado.

     * @param email correo del usuario autenticado (se guarda como subject del token)
     * @param claims información adicional que se quiere incluir dentro del token
     * @return token JWT generado
     */

    public String generarToken(String email, Map<String, Object> claims) {

        // 1. Obtener el instante actual (momento en que se crea el token)
        Instant now = Instant.now();

        // 2. Construir el token JWT utilizando la librería JJWT
        return Jwts.builder()

                // 3. Agregar claims (información adicional del usuario)
                .claims(claims)

                // 4. Definir el subject del token (identificador principal del usuario)
                .subject(email)

                // 5. Fecha de creación del token
                .issuedAt(Date.from(now))

                // 6. Fecha de expiración del token (1 hora después de su creación)
                .expiration(Date.from(now.plus(1L, ChronoUnit.HOURS)))

                // 7. Firmar el token usando la clave secreta. La firma se calcula con HMAC SHA256.
                .signWith( getKey(), Jwts.SIG.HS256)

                // 8. Compactar el token en un String final
                .compact();
    }

    /**
     * Valida y decodifica un token JWT recibido desde el cliente.

     * @param jwtString token enviado por el cliente
     * @return objeto con los claims contenidos en el token

     * @throws ExpiredJwtException si el token ya expiró
     * @throws UnsupportedJwtException si el formato del token no es soportado
     * @throws MalformedJwtException si el token está mal formado
     * @throws IllegalArgumentException si el token es nulo o inválido
     */
    public Jws<Claims> parseJwt(String jwtString) throws ExpiredJwtException,
            UnsupportedJwtException, MalformedJwtException, IllegalArgumentException {

        // 1. Crear el parser que verificará la firma del token usando la clave secreta
        JwtParser jwtParser = Jwts.parser().verifyWith( getKey() ).build();

        // 2. Parsear el token y devolver los claims si la firma es válida
        return jwtParser.parseSignedClaims(jwtString);
    }

    /**
     * Obtiene la clave secreta utilizada para firmar y verificar los tokens.

     * @return clave secreta utilizada por el algoritmo HMAC
     */
    private SecretKey getKey() {

        // 1. Clave secreta usada para firmar el token
        String claveSecreta = "secretsecretsecretsecretsecretsecretsecretsecret12345";

        // 2. Convertir la clave a bytes
        byte[] secretKeyBytes = claveSecreta.getBytes();

        // 3. Crear la clave criptográfica compatible con el algoritmo HMAC
        return Keys.hmacShaKeyFor(secretKeyBytes);
    }
}
