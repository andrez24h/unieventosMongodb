package dev.andresm.unieventosMongodb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.andresm.unieventosMongodb.dto.conex.MensajeDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro encargado de interceptar todas las peticiones HTTP
 * para validar el token JWT enviado por el cliente.

 * Su responsabilidad es:
 * - Leer el token del encabezado Authorization
 * - Validar la firma y la expiración del token
 * - Verificar los permisos del usuario según su rol
 * - Permitir o bloquear el acceso a determinados recursos

 * Este filtro se ejecuta una sola vez por cada petición HTTP.
 */
@Component
@RequiredArgsConstructor
public class FiltroToken extends OncePerRequestFilter {

    private final JWTUtils jwtUtils;

    /**
     * Método principal del filtro que intercepta cada petición HTTP.
     * <p>
     * Flujo general:
     * 1. Configura cabeceras CORS
     * 2. Permite solicitudes OPTIONS (preflight)
     * 3. Obtiene la URI de la petición
     * 4. Extrae el token del encabezado Authorization
     * 5. Valida acceso según la ruta:
     * - /api/admin → requiere rol ADMIN
     * - /api/cuenta → requiere rol CLIENTE
     * - otras rutas → acceso libre
     * 6. Valida el token (firma, expiración y contenido)
     * 7. Si todo es correcto, continúa la petición
     * 8. Si hay error, responde con JSON de error
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Configuración de cabeceras para permitir peticiones CORS
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.addHeader("Access-Control-Allow-Headers", "Origin, Accept, Content-Type, Authorization");

        // 2. Si la petición es de tipo OPTIONS (preflight de CORS) se responde inmediatamente
        if (request.getMethod().equals("OPTIONS")) {

            response.setStatus(HttpServletResponse.SC_OK);
        } else {

            // 3. Obtener la URI de la petición que se está realizando
            String requestURI = request.getRequestURI();

            // 4. Obtener el token desde el encabezado Authorization
            String token = getToken(request);
            boolean error = true;

            try {

                // 5. Validar acceso a rutas protegidas

                // ADMIN > acceso exclusivo
                if (requestURI.startsWith("/api/admin")) {

                    if (token != null) {

                        // 6. Validar el token usando JWTUtils
                        Jws<Claims> jws = jwtUtils.parseJwt(token);

                        // 7. Extraer rol del token
                        String rol = jws.getPayload().get("rol").toString();

                        // 8. Validar que el rol sea ADMIN
                        if (!rol.equals("ADMIN")) {
                            crearRespuestaError("No tiene permisos para acceder a este recurso",
                                    HttpServletResponse.SC_FORBIDDEN, response);
                            return;
                        }

                        // 9. Acceso permitido
                        error = false;

                    } else {
                        crearRespuestaError("No tiene permisos para acceder a este recurso",
                                HttpServletResponse.SC_FORBIDDEN, response);
                        return;
                    }

                }

                // CLIENTE > acceso a sus recursos
                else if (requestURI.startsWith("/api/cuenta")) {

                    if (token != null) {

                        // 10. Validar el token usando JWTUtils
                        Jws<Claims> jws = jwtUtils.parseJwt(token);

                        // 11. Extraer rol del token
                        String rol = jws.getPayload().get("rol").toString();

                        // 12. Validar que el rol sea CLIENTE
                        if (!rol.equals("CLIENTE")) {
                            crearRespuestaError("No tiene permisos para acceder a este recurso",
                                    HttpServletResponse.SC_FORBIDDEN, response);
                            return;
                        }

                        // 13. Acceso permitido
                        error = false;

                    } else {
                        crearRespuestaError("No tiene permisos para acceder a este recurso",
                                HttpServletResponse.SC_FORBIDDEN, response);
                        return;
                    }
                }

                // 14. Rutas públicas (ej: /api/auth)
                else {
                    error = false;
                }

            } catch (MalformedJwtException | SignatureException e) {

                // 15. Error cuando el token tiene formato inválido o firma incorrecta
                crearRespuestaError("El token es incorrecto", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
                return;

            } catch (ExpiredJwtException e) {

                // 16. Error cuando el token ha expirado
                crearRespuestaError("El token está vencido", HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
                return;

            } catch (Exception e) {

                // 17. Manejo de errores generales
                crearRespuestaError(e.getMessage(), HttpServletResponse.SC_INTERNAL_SERVER_ERROR, response);
            }

            // 18. Si no hubo errores se continúa con la cadena de filtros
            if (!error) {

                filterChain.doFilter(request, response);
            }
        }
    }

    /**
     * Obtiene el token JWT desde el encabezado Authorization.
     *
     * @param req petición HTTP recibida
     * @return token JWT o null si no existe
     */
    private String getToken(HttpServletRequest req) {
        // 1. Obtener el encabezado Authorization
        String header = req.getHeader("Authorization");

        // 2. Verificar que el encabezado exista y comience con "Bearer "
        if (header != null && header.startsWith("Bearer "))

            // 3. Retornar el token eliminando el prefijo "Bearer "
            return header.replace("Bearer ", "");

        // 4. Retornar null si no hay token
        return null;
    }

    /**
     * Construye una respuesta de error en formato JSON
     * cuando el token es inválido o el usuario no tiene permisos.
     *
     * @param mensaje     mensaje de error
     * @param codigoError código HTTP de error
     * @param response    respuesta HTTP
     */
    private void crearRespuestaError(String mensaje, int codigoError, HttpServletResponse
            response) throws IOException {
        // 1. Construir el DTO de respuesta
        MensajeDTO<String> dto = new MensajeDTO<>(true, mensaje, null);

        // 2. Configurar la respuesta HTTP
        response.setContentType("application/json");
        response.setStatus(codigoError);

        // 3. Convertir el DTO a JSON y enviarlo al cliente
        response.getWriter().write(new ObjectMapper().writeValueAsString(dto));
        response.getWriter().flush();
        response.getWriter().close();
    }
}


// ========================================================================
// Método alternativo para validar token y rol (no usado actualmente)
// Puede reutilizarse para simplificar la validación en el filtro
// ========================================================================
    /**
     * - Valida si un token JWT es válido y si el usuario tiene el rol requerido.

     * Este método:
     * - Verifica que el token no sea nulo
     * - Decodifica y valida el token usando JWTUtils
     * - Extrae el rol almacenado en los claims
     * - Compara el rol del token con el rol esperado

     * @param token token JWT enviado por el cliente
     * @param rol rol que se requiere para acceder al recurso
     * @return true si hay error (token inválido o rol incorrecto),
     *         false si el token es válido y el rol coincide

    private boolean validarToken(String token, Rol rol) {

        // 1. Inicializar la variable de control en true (asume error por defecto)
        boolean error = true;

        // 2. Verificar que el token no sea nulo
        if (token != null) {

            // 3. Parsear el token para obtener su contenido (claims)
            Jws<Claims> jws = jwtUtils.parseJwt(token);

            // 4. Comparar el rol del token con el rol requerido
            if (Rol.valueOf(jws.getPayload().get("rol").toString()) == rol) {

                // 5. Si el rol coincide, no hay error
                error = false;
            }
        }
        // 6. Retornar el resultado de la validación
        return error;
    }*/

/**
 * Jws<Claims> jws = jwtUtils.parseJwt(token)

 * Ese método hace lo mismo de validarToken()

 * if (Rol.valueOf(jws.getPayload().get("rol").toString()) == rol) {
 *     error = false;
 * }
 */