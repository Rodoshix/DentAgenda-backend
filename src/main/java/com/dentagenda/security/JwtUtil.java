package com.dentagenda.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // Clave secreta de al menos 256 bits (32 caracteres)
    private static final String SECRET = "12345678901234567890123456789012"; 

    // Tiempo de expiración (ej: 1 hora)
    private static final long EXPIRATION_TIME_MS = 3600000;

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String rut, String role) {
        return Jwts.builder()
                .setSubject(rut)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

     //Método para extraer el "username" desde el token
     public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    //Método para validar el token (por expiración)
    public boolean validateToken(String token) {
        try {
            return !extractAllClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractRol(String token) {
        return extractAllClaims(token).get("role", String.class);
    }
}