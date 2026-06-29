package com.library.bff.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtTokenServiceTest {

    private static final String SECRET = "mi_clave_super_secreta_muy_larga_12345678901234567890";

    @Test
    void extractSubject_whenTokenIsValid_shouldReturnEmail() throws Exception {
        JwtTokenService jwtTokenService = new JwtTokenService(SECRET);
        String token = generateToken("test@correo.com");

        String subject = jwtTokenService.extractSubject(token);

        assertEquals("test@correo.com", subject);
    }

    @Test
    void extractSubject_whenTokenIsInvalid_shouldThrowException() {
        JwtTokenService jwtTokenService = new JwtTokenService(SECRET);

        assertThrows(Exception.class, () -> jwtTokenService.extractSubject("token-invalido"));
    }

    private String generateToken(String email) throws Exception {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(3600)))
                .signWith(signingKey())
                .compact();
    }

    private SecretKey signingKey() throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] key = digest.digest(SECRET.getBytes(StandardCharsets.UTF_8));
        return Keys.hmacShaKeyFor(key);
    }
}
