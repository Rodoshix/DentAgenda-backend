package com.dentagenda.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

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
}