package com.cagst.swkroa.service.user;

import java.time.LocalDateTime;
import java.util.Optional;

import com.cagst.swkroa.service.security.SecurityPolicy;
import com.cagst.swkroa.service.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * An implementation of the {@link UserService}.
 *
 * @author Craig Gaskill
 */
@Service
public class UserServiceImpl implements UserService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final SecurityService securityService;

  public UserServiceImpl(UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         SecurityService securityService
  ) {
    this.userRepository  = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.securityService = securityService;
  }

  @Override
  public Mono<User> loginAttempt(String username, String password, String remoteAddress) {
    Optional<User> findUser = userRepository.getUserByUsername(username);
    if (!findUser.isPresent()) {
      LOGGER.warn("Username [{}] was not found.", username);
      loginFailure(username, "User not found.");
      return Mono.empty();
    }

    // increment the login attempts
    User user = userRepository.incrementLoginAttempts(findUser.get());

    // validate the password
    if (!passwordEncoder.matches(password, user.password())) {
      LOGGER.warn("Invalid password for user [{}]", username);
      loginFailure(username, "Password invalid");
      return Mono.empty();
    }

    // retrieve SecurityPolicy for user
    SecurityPolicy securityPolicy = securityService.getSecurityPolicy(user);

    // check to see if the account is already locked and should therefore be unlocked
    int lockedInMinutes = securityPolicy.lockedInMinutes();
    if (user.lockedDateTime() != null && lockedInMinutes > 0) {
      LocalDateTime lockedDateTime = user.lockedDateTime();
      LocalDateTime unlockAfter = lockedDateTime.plusMinutes(lockedInMinutes);

      if (LocalDateTime.now().isAfter(unlockAfter)) {
        user = unlockAccount(user.userId(), user);
        LOGGER.debug("User account [{}] was unlocked", username);
      }
    }

    // if the account isn't locked, see if it needs to be locked (because they exceeded their login attempts)
    if (user.lockedDateTime() == null) {
      // if the security policy has a maximum number of login attempts > 0
      // and we have exceeded the number of maximum attempts
      // then lock the account.
      if (securityPolicy.maxAttempts() > 0 && user.loginAttempts() > securityPolicy.maxAttempts()) {
        user = lockAccount(user.userId(), user);
        LOGGER.warn("User account [{}] was locked", username);
      }
    }

    user = loginSuccessful(user, remoteAddress);

    return Mono.just(user);
  }

  @Override
  public User loginSuccessful(User user, String ipAddress) throws IllegalArgumentException {
    User signedInUser = userRepository.loginSuccessful(user, ipAddress);

//    List<Role> roles = roleRepo.getRolesForUser(signedInUser);
//    if (CollectionUtils.isEmpty(roles)) {
//      LOGGER.warn("No roles found for user [{}].", signedInUser.getUsername());
//    } else {
//      for (Role role : roles) {
//        user.addRole(role);
//        user.addGrantedAuthority(role.getRoleKey());
//      }
//    }

    return signedInUser;
  }

  @Override
  public void loginFailure(String username, String message) throws IllegalArgumentException {

  }

  @Override
  public User lockAccount(long userId, User user) {
    return userRepository.lockUserAccount(userId, user);
  }

  @Override
  public User unlockAccount(long userId, User user) {
    return userRepository.unlockUserAccount(userId, user);
  }
}
