package com.cagst.swkroa.service.security.token;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
  @DisplayName("when finding a token")
  class WhenFindToken {
    @Nested
    @DisplayName("by Ident")
    class ByIdent {
      @Test
      @DisplayName("should return an empty Mono when no valid token is found")
      void testInvalid() {
        repo.findTokenByIdent(11, "20249447-9960-4613-8fa8-86ee91785be0")
            .subscribe(token -> fail("should not emit an event since the token was not found"));
      }

      @Test
      @DisplayName("should return true when the token is valid")
      void testValid() {
        repo.findTokenByIdent(11, "ec3981aa-f8a8-422d-a0b5-e389510ed63d")
            .subscribe(token -> assertAll("Ensure the token",
                () -> assertNotNull(token, "was found"),
                () -> assertFalse(token.used(), "hasn't been used"))
            );
      }
    }
  }

  @Nested
  @DisplayName("when inserting a token")
  class WhenInsertToken {
    @Test
    @DisplayName("should insert a new token")
    void testInsert() {
      Token newToken = Token.builder()
          .tokenIdent(UUID.randomUUID())
          .userId(11L)
          .expiryDateTime(OffsetDateTime.now().plusMinutes(15))
          .build();

      repo.insertToken(11L, newToken)
          .subscribe(token ->
              assertAll("Ensure the token",
                  () -> assertNotNull(token, "is valid"),
                  () -> assertNotNull(token.tokenId(), "has a valid ID"))
          );
    }
  }

  @Nested
  @DisplayName("when updating a token")
  class WhenUpdateToken {
    @Test
    @DisplayName("should update an existing token")
    void testUpdate() {
      repo.findTokenByIdent(11, "ec3981aa-f8a8-422d-a0b5-e389510ed63d")
          .subscribe(token -> {
            assertAll("Ensure the token",
                    () -> assertNotNull(token, "was found"),
                    () -> assertFalse(token.used(), "hasn't been used"));

            repo.updateToken(11L, token.toBuilder().used(true).build());

            repo.findTokenByIdent(11, "ec3981aa-f8a8-422d-a0b5-e389510ed63d")
                .subscribe(updatedToken ->
                    assertAll("Ensure the token",
                        () -> assertNotNull(updatedToken, "was found"),
                        () -> assertTrue(updatedToken.used(), "has been used"))
                );
          });
    }
  }
}
