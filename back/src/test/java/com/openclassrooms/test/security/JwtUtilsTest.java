package com.openclassrooms.test.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

class JwtUtilsTest {

    // HS512 requires a key of at least 64 bytes, hence the length
    private static final String TEST_SECRET = "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef";
    private static final String OTHER_SECRET = "fedcba9876543210fedcba9876543210fedcba9876543210fedcba9876543210fedcba9876543210fedcba9876543210fedcba9876543210fedcba98765432";
    private static final int TEST_EXPIRATION_MS = 3_600_000;

    JwtUtils jwtUtils = new JwtUtils();

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", TEST_EXPIRATION_MS);
    }

    @Test
    void generateJwtTokenShouldReturnATokenWithTheUsernameAsSubject() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder().id(1L).username("yoga@studio.com").build();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        String token = jwtUtils.generateJwtToken(authentication);

        assertNotNull(token);
        assertEquals("yoga@studio.com", jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    void getUserNameFromJwtTokenShouldReturnTheSubject() {
        String token = buildToken("yoga@studio.com", TEST_SECRET, TEST_EXPIRATION_MS);

        assertEquals("yoga@studio.com", jwtUtils.getUserNameFromJwtToken(token));
    }

    @Test
    void validateJwtTokenShouldReturnTrueForAValidToken() {
        String token = buildToken("yoga@studio.com", TEST_SECRET, TEST_EXPIRATION_MS);

        assertTrue(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtTokenShouldReturnFalseForAMalformedToken() {
        assertFalse(jwtUtils.validateJwtToken("not-a-jwt-token"));
    }

    @Test
    void validateJwtTokenShouldReturnFalseForAnExpiredToken() {
        String token = buildToken("yoga@studio.com", TEST_SECRET, -1_000);

        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtTokenShouldReturnFalseForAnInvalidSignature() {
        String token = buildToken("yoga@studio.com", OTHER_SECRET, TEST_EXPIRATION_MS);

        assertFalse(jwtUtils.validateJwtToken(token));
    }

    @Test
    void validateJwtTokenShouldReturnFalseForAnEmptyToken() {
        assertFalse(jwtUtils.validateJwtToken(""));
    }

    private static String buildToken(String subject, String secret, long expirationMs) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
}
