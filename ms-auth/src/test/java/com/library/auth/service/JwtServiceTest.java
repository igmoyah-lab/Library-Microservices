package com.library.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    private static final String SECRET = "mi_clave_super_secreta_muy_larga_12345678901234567890";

    @Test
    void generateToken_shouldCreateValidJwtWithEmailSubject() throws Exception {
        JwtService jwtService = new JwtService(SECRET, 2);

        String token = jwtService.generateToken("test@correo.com");

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertEquals(3, token.split("\\.").length);

        String subject = Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        assertEquals("test@correo.com", subject);
    }

    private SecretKey signingKey() throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] key = digest.digest(SECRET.getBytes(StandardCharsets.UTF_8));
        return Keys.hmacShaKeyFor(key);
    }
}
