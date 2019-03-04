package com.cagst.swkroa.service.security.token;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.cagst.swkroa.service.internal.jdbc.BaseTestRepository;
import com.cagst.swkroa.service.internal.jdbc.StatementDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Test class for the {@link TokenRepositoryJdbc} class.
 *
 * @author Craig Gaskill
 */
@DisplayName("TokenRepositoryJdbc")
class TokenRepositoryJdbcTest extends BaseTestRepository {
  private TokenRepositoryJdbc repo;

  @BeforeEach
  void setUp() {
    repo = new TokenRepositoryJdbc(createTestDataSource());
    repo.setStatementDialect(StatementDialect.HSQLDB);
  }

  @Nested
  @DisplayName("when checking if a token is valid")
  class WhenIsTokenValid {
    @Test
    @DisplayName("should return false when the token is invalid")
    void testInvalid() {
      repo.isTokenValid(11, "20249447-9960-4613-8fa8-86ee91785be0")
          .subscribe(result -> assertFalse(result, "Ensure the result is invalid"));
    }

    @Test
    @DisplayName("should return true when the token is valid")
    void testValid() {
      repo.isTokenValid(11, "ec3981aa-f8a8-422d-a0b5-e389510ed63d")
          .subscribe(result -> assertTrue(result, "Ensure the result is valid"));
    }
  }

  @Nested
  @DisplayName("when inserting a token")
  class WhenInsertToken {
    @Test
    @DisplayName("should insert a new token")
    void testInsert() {
      Token newToken = Token.builder()
          .token(UUID.randomUUID())
          .userId(11L)
          .expiryDateTime(OffsetDateTime.now().plusMinutes(15))
          .build();

      repo.insertToken(newToken);
    }
  }
}
