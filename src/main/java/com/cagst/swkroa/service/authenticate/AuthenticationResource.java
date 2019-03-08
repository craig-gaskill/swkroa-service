package com.cagst.swkroa.service.authenticate;

import java.net.InetSocketAddress;
import java.time.OffsetDateTime;
import java.util.UUID;
import javax.inject.Inject;

import com.cagst.swkroa.service.security.JWTService;
import com.cagst.swkroa.service.security.LoginStatus;
import com.cagst.swkroa.service.security.token.Token;
import com.cagst.swkroa.service.security.token.TokenRepository;
import com.cagst.swkroa.service.user.UserService;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Defines endpoints used for authentication.
 *
 * @author Craig Gaskill
 */
@RestController
@RequestMapping("auth")
public class AuthenticationResource {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationResource.class);

  private static final long ACCESS_EXPIRY_IN_MINUTES  = 5L;
  private static final long REFRESH_EXPIRY_IN_MINUTES = 10L;

  private final UserService userService;
  private final TokenRepository tokenRepository;
  private final JWTService jwtService;

  @Inject
  public AuthenticationResource(UserService userService,
                                TokenRepository tokenRepository,
                                JWTService jwtService
  ) {
    this.userService     = userService;
    this.tokenRepository = tokenRepository;
    this.jwtService      = jwtService;
  }

  /**
   * The {@code /login} endpoint to authenticate a user by username / password.
   *
   * @param loginRequest
   *    A {@link LoginRequest} that contains the username / password of the User trying to authenticate.
   * @param request
   *    The {@link ServerHttpRequest} that initiated the call.
   *
   * @return A {@link Mono} containing the {@link LoginResponse} that may contain a JWT access and refresh token
   * if the authentication was successful, otherwise it will simply contain the Status of why the login failed.
   */
  @Transactional
  @PostMapping("login")
  public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest loginRequest,
                                                   ServerHttpRequest request
  ) {
    LOGGER.debug("Received request to login.");

    String ipAddress = null;
    InetSocketAddress remoteAddress = request.getRemoteAddress();
    if (remoteAddress != null) {
      ipAddress = remoteAddress.toString();
    }

    return userService.loginAttempt(loginRequest.username(), loginRequest.password(), ipAddress)
        .flatMap(user -> {
          if (!user.active()) {
            return Mono.just(generateResponse(LoginStatus.DISABLED, null, null));
          } else if (user.lockedDateTime() != null) {
            return Mono.just(generateResponse(LoginStatus.LOCKED, null, null));
          } else if (user.expiredDateTime() != null) {
            return Mono.just(generateResponse(LoginStatus.EXPIRED, null, null));
          } else {
            return Mono.just(generateResponse(LoginStatus.VALID, user.userId(), user.temporary()));
          }
        })
        .defaultIfEmpty(new ResponseEntity<>(LoginResponse.builder()
            .loginStatus(LoginStatus.INVALID)
            .build(), HttpStatus.UNAUTHORIZED)
        );
  }

  /**
   * The {@code /refresh} endpoint to retrieve a new JWT access token based upon the JWT refresh token.
   */
  @Transactional
  @PostMapping("refresh")
  public Mono<ResponseEntity<LoginResponse>> refresh(@RequestBody String refreshToken) {
    return ReactiveSecurityContextHolder.getContext()
        .map(securityContext -> Long.valueOf(securityContext.getAuthentication().getPrincipal().toString()))
        .flatMap(userId -> tokenRepository.findToken(userId, refreshToken)
            .map(token -> {
              tokenRepository.updateToken(token.toBuilder().used(true).build());
                return generateResponse(LoginStatus.VALID, userId, false);
            })
            .defaultIfEmpty(generateResponse(LoginStatus.INVALID, null, null))
        );
  }

  private ResponseEntity<LoginResponse> generateResponse(LoginStatus loginStatus, Long userId, Boolean temporary) {
    switch (loginStatus) {
      case DISABLED:
      case LOCKED:
      case EXPIRED:
        return new ResponseEntity<>(LoginResponse.builder().loginStatus(loginStatus).build(), HttpStatus.UNAUTHORIZED);

      case VALID: {
        OffsetDateTime expiryDateTime = OffsetDateTime.now().plusMinutes(ACCESS_EXPIRY_IN_MINUTES);

        Token refreshToken = Token.builder()
            .token(UUID.randomUUID())
            .userId(userId)
            .expiryDateTime(expiryDateTime.plusMinutes(REFRESH_EXPIRY_IN_MINUTES))
            .build();

        tokenRepository.insertToken(refreshToken);

        return ResponseEntity.ok(LoginResponse.builder()
            .loginStatus(BooleanUtils.toBoolean(temporary) ? LoginStatus.TEMPORARY : LoginStatus.VALID)
            .accessToken(jwtService.generateAccessToken(userId, expiryDateTime))
            .refreshToken(refreshToken.token().toString())
            .build());
      }

      default:
        return new ResponseEntity<>(LoginResponse.builder().loginStatus(LoginStatus.INVALID).build(), HttpStatus.UNAUTHORIZED);
    }
  }
}
