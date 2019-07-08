package com.cagst.swkroa.service.security;

import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link JWTServiceImpl} class.
 *
 * @author Craig Gaskill
 */
@DisplayName("JWTSecurityImpl")
class JWTSecurityImplTest {
  private Algorithm algorithm = Algorithm.HMAC512("secret");

  private JWTServiceImpl jwtService = new JWTServiceImpl(algorithm);

  @Test
  @DisplayName("should create and verify a Token")
  void testCreateAndVerify() {
    OffsetDateTime expiryDateTime = OffsetDateTime.now().plusMinutes(15);

    String token = jwtService.generateAccessToken(1L, expiryDateTime);
    assertAll("Ensure the token",
        () -> assertNotNull(token, "exists"),
        () -> assertFalse(token.isEmpty(), "is not empty"));

    boolean valid = jwtService.isTokenValid(token);
    assertTrue(valid, "is valid");

    long userId = jwtService.getUserId(token);
    assertEquals(1L, userId, "is the correct UserID");
  }
}
