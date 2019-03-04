package com.cagst.swkroa.service.security;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;

import com.cagst.swkroa.service.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test class for the {@link JWTServiceImpl} class.
 *
 * @author Craig Gaskill
 */
@DisplayName("JWTSecurityImpl")
class JWTSecurityImplTest {
  private final String secret = "qmvsq92lg274pgsismm2se8jjj8m8tpm";

  private JWTServiceImpl jwtService = new JWTServiceImpl(secret);

  @Test
  @DisplayName("should create and verify a Token")
  void testCreateAndVerify() {
    OffsetDateTime expiryDateTime = OffsetDateTime.now().plusMinutes(15);

    String token = jwtService.generateAccessToken(User.builder().userId(1L).build(), expiryDateTime);
    assertAll("Ensure the token",
        () -> assertNotNull(token, "exists"),
        () -> assertFalse(token.isEmpty(), "is not empty"));

    boolean valid = jwtService.isTokenValid(token);
    assertTrue(valid, "is valid");

    long userId = jwtService.getUserId(token);
    assertEquals(1L, userId, "is the correct UserID");
  }
}
