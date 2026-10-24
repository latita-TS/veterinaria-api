package com.veterinaria.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Genera y valida los tokens JWT.
 * Un JWT es como un "pase" firmado: después del login, el cliente lo envía
 * en cada petición para demostrar quién es, sin volver a mandar la contraseña.
 */
@Component
public class JwtUtil {

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtUtil(@Value("${app.jwt.secret}") String secretoBase64,
                   @Value("${app.jwt.expiration}") long expiracionMs) {
        this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretoBase64));
        this.expiracionMs = expiracionMs;
    }

    /** Crea un token para el usuario indicado. */
    public String generarToken(String username, String rol) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(username)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(clave)
                .compact();
    }

    /** Devuelve el usuario guardado dentro del token. */
    public String obtenerUsername(String token) {
        return leerClaims(token).getSubject();
    }

    /** true si la firma es correcta y el token no ha vencido. */
    public boolean esTokenValido(String token) {
        try {
            Claims claims = leerClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getExpiracionMs() {
        return expiracionMs;
    }

    private Claims leerClaims(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
