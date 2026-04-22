package cl.duoc.rednorte.auth_service.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Componente encargado de generar y validar tokens JWT.
 * Implementa el estándar JWT con firma HMAC-SHA256.
 */
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    /**
     * Construye la clave de firma segura a partir del secreto configurado.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un token JWT a partir de los datos de autenticación.
     * El token incluye: sub (email), roles, iat, exp.
     *
     * @param authentication objeto de autenticación de Spring Security
     * @return token JWT firmado como String
     */
    public String generarToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        String roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date ahora = new Date();
        Date expiracion = new Date(ahora.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("roles", roles)
                .issuedAt(ahora)
                .expiration(expiracion)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrae el email (subject) del token JWT.
     *
     * @param token token JWT
     * @return email del usuario
     */
    public String getEmailDesdeToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Extrae los roles del token JWT.
     *
     * @param token token JWT
     * @return String de roles separados por coma
     */
    public String getRolesDesdeToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("roles", String.class);
    }

    /**
     * Retorna el tiempo de expiración configurado en milisegundos.
     *
     * @return tiempo de expiración en ms
     */
    public long getExpiracionMs() {
        return jwtExpirationMs;
    }

    /**
     * Valida la firma y la expiración del token JWT.
     * Retorna false silenciosamente si el token es inválido o está expirado,
     * para no interrumpir el flujo en endpoints públicos sin token.
     *
     * @param token token JWT a validar
     * @return true si es válido, false si no
     */
    public boolean validarToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            // Token expirado — no lanzar excepción, el filtro simplemente no autentica
            return false;
        } catch (UnsupportedJwtException ex) {
            return false;
        } catch (MalformedJwtException ex) {
            return false;
        } catch (SecurityException ex) {
            return false;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
