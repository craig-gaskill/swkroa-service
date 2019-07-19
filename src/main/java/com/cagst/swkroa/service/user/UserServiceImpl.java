package com.cagst.swkroa.service.user;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.cagst.swkroa.service.person.Person;
import com.cagst.swkroa.service.person.PersonRepository;
import com.cagst.swkroa.service.security.SecurityPolicy;
import com.cagst.swkroa.service.security.SecurityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * An implementation of the {@link UserService}.
 *
 * @author Craig Gaskill
 */
@Service
/* package */ class UserServiceImpl implements UserService {
  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

  private final UserRepository userRepository;
  private final PersonRepository personRepository;
  private final PasswordEncoder passwordEncoder;
  private final SecurityService securityService;

  public UserServiceImpl(UserRepository userRepository,
                         PersonRepository personRepository,
                         PasswordEncoder passwordEncoder,
                         SecurityService securityService
  ) {
    this.userRepository  = userRepository;
    this.personRepository = personRepository;
    this.passwordEncoder = passwordEncoder;
    this.securityService = securityService;
  }

  @Override
  public Mono<User> loginAttempt(@NonNull String username, @NonNull String password, @Nullable String remoteAddress) {
    LOGGER.debug("Calling loginAttempt for [{}]", username);

    // retrieve the SecurityPolicy
    SecurityPolicy securityPolicy = securityService.getDefaultSecurityPolicy();

    return userRepository.getUserByUsername(username)
        .flatMap(userRepository::incrementLoginAttempts)
        .flatMap(usr -> {
          // validate the password
          if (!passwordEncoder.matches(password, usr.password())) {
            LOGGER.warn("Invalid password for user [{}]", username);

            // if the account isn't locked, see if it needs to be locked (because they exceeded their login attempts)
            if (usr.lockedDateTime() == null) {
              // if the security policy has a maximum number of login attempts > 0
              // and we have exceeded the number of maximum attempts
              // then lock the account.
              if (securityPolicy.maxAttempts() > 0 && usr.loginAttempts() > securityPolicy.maxAttempts()) {
                LOGGER.warn("User account [{}] was locked", username);
                return userRepository.lockUserAccount(usr.userId(), usr);
              }
            }

            return Mono.empty();
          } else {
            return Mono.just(usr);
          }
        })
        .flatMap(usr -> {
          // check to see if the account is already locked and should therefore be unlocked
          int lockedInMinutes = securityPolicy.lockedInMinutes();
          if (usr.lockedDateTime() != null && lockedInMinutes > 0) {
            LocalDateTime lockedDateTime = usr.lockedDateTime();
            LocalDateTime unlockAfter = lockedDateTime.plusMinutes(lockedInMinutes);

            if (LocalDateTime.now().isAfter(unlockAfter)) {
              LOGGER.debug("User account [{}] was unlocked", username);
              return userRepository.unlockUserAccount(usr.userId(), usr);
            }
          }

          return Mono.just(usr);
        })
        .flatMap(usr -> userRepository.loginSuccessful(usr, remoteAddress))
        .map(usr -> UserConverter.convert(usr, null));
  }

  @Override
  public Mono<User> lockAccount(long userId, Mono<User> user) {
    return user.flatMap(usr -> userRepository.lockUserAccount(userId, UserConverter.convert(usr))
          .map(u -> UserConverter.convert(u, usr.person())));
  }

  @Override
  public Mono<User> unlockAccount(long userId, Mono<User> user) {
    return user.flatMap(usr -> userRepository.unlockUserAccount(userId, UserConverter.convert(usr))
        .map(u -> UserConverter.convert(u, usr.person())));
  }

  @Override
  public Flux<User> getUsers() {
    return userRepository.getUsers()
        .collectList()
        .map(users -> {
          Set<Long> personIds = users.stream().map(UserEntity::personId).collect(Collectors.toSet());
          return personRepository.getPeopleByIds(personIds)
              .collect(Collectors.toMap(Person::personId, Function.identity()))
              .map(peopleMap ->
                  users.parallelStream().map(user -> UserConverter.convert(user, peopleMap.get(user.personId()))));
        }).flatMapMany(results -> results.flatMapMany(Flux::fromStream));
  }
}
