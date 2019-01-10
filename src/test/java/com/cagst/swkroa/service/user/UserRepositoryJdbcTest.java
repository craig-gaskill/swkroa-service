package com.cagst.swkroa.service.user;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import com.cagst.swkroa.service.internal.jdbc.BaseTestRepository;
import com.cagst.swkroa.service.internal.jdbc.StatementDialect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
    @DisplayName("should return an empty optional when no User is found")
    void testNotFound() {
      Optional<User> checkUser = repo.getUserByUsername("billybob");
      assertFalse(checkUser.isPresent(), "Ensure user was not found.");
    }

    @Test
    @DisplayName("should return a populated optional when a User is found")
    void testFound() {
      Optional<User> checkUser = repo.getUserByUsername("cgaskill");

      assertAll("Ensure the User",
          () -> assertNotNull(checkUser, "is valid"),
          () -> assertTrue(checkUser.isPresent(), "was found"),
          () -> assertEquals("cgaskill", checkUser.get().username(), "is the correct User"),
          () -> assertNull(checkUser.get().lastLoginDateTime(), "has not logged in"),
          () -> assertNull(checkUser.get().lastLoginIp(), "has not logged in"));
    }

    @Test
    @DisplayName("should return a User that will expire but hasn't expired yet.")
    void testWillExpired() {
      Optional<User> checkUser = repo.getUserByUsername("temp");
      assertAll("Ensure the User",
          () -> assertNotNull(checkUser, "is valid"),
          () -> assertTrue(checkUser.isPresent(), "was found"),
          () -> assertEquals("temp", checkUser.get().username(), "is the correct User"),
          () -> assertNotNull(checkUser.get().expiredDateTime(), "has an expiry date"));
    }

    @Test
    @DisplayName("should return a User that has expired")
    void testGetUserByUsername_Expired() {
      Optional<User> checkUser = repo.getUserByUsername("expire");
      assertAll("Ensure the User",
          () -> assertNotNull(checkUser, "is valid"),
          () -> assertTrue(checkUser.isPresent(), "was found"),
          () -> assertEquals("expire", checkUser.get().username(), "is the correct User"),
          () -> assertNotNull(checkUser.get().expiredDateTime(), "has an expiry date"));
    }

    @Test
    @DisplayName("should return a User that is locked")
    void testGetUserByUsername_Locked() {
      Optional<User> checkUser = repo.getUserByUsername("locked");
      assertAll("Ensure the User",
          () -> assertNotNull(checkUser, "is valid"),
          () -> assertTrue(checkUser.isPresent(), "was found"),
          () -> assertEquals("locked", checkUser.get().username(), "is the correct User"),
          () -> assertEquals(5, checkUser.get().loginAttempts(), "exceeds the allowed login attempts"),
          () -> assertNotNull(checkUser.get().lockedDateTime(), "is locked"));
    }
  }

  @Nested
  @DisplayName("when incrementing login attempts")
  class WhenIncrementLoginAttempt {
    @Test
    @DisplayName("should increment the login attempts")
    void testIncrementLoginAttempt() {
      Optional<User> checkUser1 = repo.getUserByUsername("cgaskill");
      assertTrue(checkUser1.isPresent(), "Ensure user was found.");

      User user1 = checkUser1.get();
      assertAll("Ensure we found the correct user",
          () -> assertEquals("cgaskill", user1.username(), "(check username)"),
          () -> assertEquals(0, user1.loginAttempts(), "(check sign-in attempts)"),
          () -> assertEquals(0, user1.updateCount(), "(check update count)"));

      User user = repo.incrementLoginAttempts(user1);
      assertAll("Ensure the user",
          () -> assertNotNull(user, "id still valid"),
          () -> assertEquals("cgaskill", user.username(), "is the same user"),
          () -> assertEquals(1, user.loginAttempts(), "login attempts has been incremented."));

      Optional<User> checkUser2 = repo.getUserByUsername("cgaskill");
      assertTrue(checkUser2.isPresent(), "Ensure user was found.");

      User user2 = checkUser2.get();
      assertAll("Ensure we found",
          () -> assertEquals("cgaskill", user2.username(), "the correct user (check username)"),
          () -> assertEquals(1, user2.loginAttempts(), "and the user's login attempts is correct"));
    }
  }

  @Nested
  @DisplayName("when a login is successful")
  class WhenLoginSuccessful {
    /**
     * Tests the signinSuccessful method.
     */
    @Test
    @DisplayName("should reset the login attempts, last login date/time, and last login ip")
    void testLoginSuccessful() {
      String ipAddress = "127.0.0.1";

      Optional<User> checkUser1 = repo.getUserByUsername("temp");
      assertTrue(checkUser1.isPresent(), "Ensure user was found.");

      User user1 = checkUser1.get();
      assertAll("Ensure the User",
          () -> assertEquals("temp", user1.username(), "is the correct user"),
          () -> assertNull(user1.lastLoginDateTime(), "has not signed in (no last sign-in date"),
          () -> assertNull(user1.lastLoginIp(), "has not signed in (no last sign-in ip"),
          () -> assertEquals(0, user1.updateCount(), "has not been updated"));

      User user = repo.loginSuccessful(user1, ipAddress);
      assertEquals("temp", user.username(), "Ensure it is the correct user (check username).");

      assertAll("Ensure the User",
          () -> assertNotNull(user, "is valid"),
          () -> assertEquals("temp", user.username(), "is the correct user"));

      Optional<User> checkUser2 = repo.getUserByUsername("temp");
      assertTrue(checkUser2.isPresent(), "Ensure user was found.");

      User user2 = checkUser2.get();
      assertAll("Ensure the User",
          () -> assertEquals("temp", user2.username(), "is the correct user"),
          () -> assertNotNull(user2.lastLoginDateTime(), "has signed in (last sign-in date"),
          () -> assertEquals(ipAddress, user2.lastLoginIp(), "has signed in (last sign-in ip"));
    }
  }

  @Nested
  @DisplayName("when locking the User's account")
  class WhenLockAccount {
    @Test
    @DisplayName("should lock the User's account")
    void testLockUserAccount() {
      Optional<User> checkUser1 = repo.getUserByUsername("cgaskill");
      assertTrue(checkUser1.isPresent(), "Ensure user was found.");

      User user1 = checkUser1.get();
      assertNull(user1.lockedDateTime(), "is not locked");

      User user2 = repo.lockUserAccount(1, user1);
      assertNotNull(user2.lockedDateTime(), "Ensure user is valid.");

      Optional<User> checkUser3 = repo.getUserByUsername("cgaskill");
      assertAll("Ensure the user account",
          () -> assertNotNull(checkUser3, "is valid"),
          () -> assertTrue(checkUser3.isPresent(), "was found"),
          () -> assertNotNull(checkUser3.get().lockedDateTime(), "is locked"));
    }
  }

  @Nested
  @DisplayName("when unlocking the User's account")
  class WhenUnlockAccount {
    @Test
    @DisplayName("should unlock the Users' account")
    void testUnlockUserAccount() {
      Optional<User> checkUser1 = repo.getUserByUsername("locked");
      assertTrue(checkUser1.isPresent(), "Ensure user was found.");

      User user1 = checkUser1.get();
      assertNotNull(user1.lockedDateTime(), "Ensure user account is locked.");

      User user2 = repo.unlockUserAccount(1, user1);
      assertNull(user2.lockedDateTime(), "Ensure user account is unlocked.");

      Optional<User> checkUser3 = repo.getUserByUsername("locked");
      assertAll("Ensure the user account",
          () -> assertNotNull(checkUser3, "is valid"),
          () -> assertTrue(checkUser3.isPresent(), "was found"),
          () -> assertNull(checkUser3.get().lockedDateTime(), "is unlocked"));
    }
  }
}
