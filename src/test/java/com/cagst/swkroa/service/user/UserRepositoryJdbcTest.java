package com.cagst.swkroa.service.user;

import static org.junit.jupiter.api.Assertions.*;

import com.cagst.common.jdbc.BaseTestRepository;
import com.cagst.common.jdbc.StatementDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Test class for the {@link UserRepositoryJdbc} class.
 *
 * @author Craig Gaskill
 */
@DisplayName("UserRepositoryJdbc")
class UserRepositoryJdbcTest extends BaseTestRepository {
  private UserRepositoryJdbc repo;

  @BeforeEach
  void setUp() {
    repo = new UserRepositoryJdbc(createTestDataSource());
    repo.setStatementDialect(StatementDialect.HSQLDB);
  }

  @Nested
  @DisplayName("when getting a User by username")
  class WhenGetUserByUsername {
    @Test
    @DisplayName("should not emit when no User is found")
    void testNotFound() {
      StepVerifier.create(repo.getUserByUsername("billybob"))
          .verifyComplete();
    }

    @Test
    @DisplayName("should return a populated optional when a User is found")
    void testFound() {
      StepVerifier.create(repo.getUserByUsername("cgaskill"))
          .assertNext(result -> assertAll("Ensure the User",
              () -> assertNotNull(result, "is valid"),
              () -> assertEquals("cgaskill", result.username(), "is the correct User"),
              () -> assertNull(result.lastLoginDateTime(), "has not logged in"),
              () -> assertNull(result.lastLoginIp(), "has not logged in"))
          )
          .verifyComplete();
    }

    @Test
    @DisplayName("should return a User that will expire but hasn't expired yet.")
    void testWillExpired() {
      StepVerifier.create(repo.getUserByUsername("temp"))
          .assertNext(result -> assertAll("Ensure the User",
              () -> assertNotNull(result, "is valid"),
              () -> assertEquals("temp", result.username(), "is the correct User"),
              () -> assertNotNull(result.expiredDateTime(), "has an expiry date"))
          )
          .verifyComplete();
    }

    @Test
    @DisplayName("should return a User that has expired")
    void testGetUserByUsername_Expired() {
      StepVerifier.create(repo.getUserByUsername("expire"))
          .assertNext(result -> assertAll("Ensure the User",
              () -> assertNotNull(result, "is valid"),
              () -> assertEquals("expire", result.username(), "is the correct User"),
              () -> assertNotNull(result.expiredDateTime(), "has an expiry date"))
          )
          .verifyComplete();
    }

    @Test
    @DisplayName("should return a User that is locked")
    void testGetUserByUsername_Locked() {
      StepVerifier.create(repo.getUserByUsername("locked"))
          .assertNext(result -> assertAll("Ensure the User",
              () -> assertNotNull(result, "is valid"),
              () -> assertEquals("locked", result.username(), "is the correct User"),
              () -> assertEquals(5, result.loginAttempts(), "exceeds the allowed login attempts"),
              () -> assertNotNull(result.lockedDateTime(), "is locked"))
          )
          .verifyComplete();
    }
  }

  @Nested
  @DisplayName("when incrementing login attempts")
  class WhenIncrementLoginAttempt {
    @Test
    @DisplayName("should increment the login attempts")
    void testIncrementLoginAttempt() {
      StepVerifier.create(repo.getUserByUsername("cgaskill")
          .flatMap(usr -> repo.incrementLoginAttempts(Mono.just(usr)))
          .flatMap(__ -> repo.getUserByUsername("cgaskill"))
      ).assertNext(user -> assertAll("Ensure we found the correct user",
          () -> assertEquals("cgaskill", user.username(), "(check username)"),
          () -> assertEquals(1, user.loginAttempts(), "(check sign-in attempts)"))
      ).verifyComplete();
    }
  }

  @Nested
  @DisplayName("when a login is successful")
  class WhenLoginSuccessful {
    @Test
    @DisplayName("should reset the login attempts, last login date/time, and last login ip")
    void testLoginSuccessful() {
      String ipAddress = "127.0.0.1";

      StepVerifier.create(repo.getUserByUsername("temp")
          .flatMap(usr -> repo.loginSuccessful(Mono.just(usr), ipAddress))
          .flatMap(__ -> repo.getUserByUsername("temp"))
      ).assertNext(user -> assertAll("Ensure the User",
          () -> assertEquals("temp", user.username(), "is the correct user"),
          () -> assertNotNull(user.lastLoginDateTime(), "has signed in (last sign-in date"),
          () -> assertEquals(ipAddress, user.lastLoginIp(), "has signed in (last sign-in ip"))
      ).verifyComplete();
    }
  }

  @Nested
  @DisplayName("when locking the User's account")
  class WhenLockAccount {
    @Test
    @DisplayName("should lock the User's account")
    void testLockUserAccount() {
      StepVerifier.create(repo.getUserByUsername("cgaskill")
          .flatMap(usr -> repo.lockUserAccount(1L, Mono.just(usr)))
          .flatMap(__ -> repo.getUserByUsername("cgaskill"))
      ).assertNext(user -> assertAll("Ensure the user account",
          () -> assertNotNull(user, "is valid"),
          () -> assertNotNull(user.lockedDateTime(), "is locked"))
      ).verifyComplete();
    }
  }

  @Nested
  @DisplayName("when unlocking the User's account")
  class WhenUnlockAccount {
    @Test
    @DisplayName("should unlock the Users' account")
    void testUnlockUserAccount() {
      StepVerifier.create(repo.getUserByUsername("locked")
          .flatMap(usr -> repo.unlockUserAccount(1L, Mono.just(usr)))
          .flatMap(__ -> repo.getUserByUsername("locked"))
      ).assertNext(user -> assertAll("Ensure the user account",
          () -> assertNotNull(user, "is valid"),
          () -> assertNull(user.lockedDateTime(), "is unlocked"))
      ).verifyComplete();
    }
  }

  @Nested
  @DisplayName("when getting users")
  class WhenGetUsers {
    @Test
    @DisplayName("should return a list of users")
    void testGetUsers() {
      repo.getUsers()
          .collectList()
          .subscribe(users ->
            assertAll("Ensure the list of users",
                () -> assertNotNull(users, "is valid"),
                () -> assertFalse(users.isEmpty(), "is not empty"),
                () -> assertEquals(7, users.size(), "has the correct number of users"))
          );
    }
  }
}
