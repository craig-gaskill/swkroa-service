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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
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
        .map(user -> {
          if (!user.active()) {
            return new ResponseEntity<>(LoginResponse.builder()
                .loginStatus(LoginStatus.DISABLED)
                .build(), HttpStatus.UNAUTHORIZED);
          } else if (user.lockedDateTime() != null) {
            return new ResponseEntity<>(LoginResponse.builder()
                .loginStatus(LoginStatus.LOCKED)
                .build(), HttpStatus.UNAUTHORIZED);
          } else if (user.expiredDateTime() != null) {
            return new ResponseEntity<>(LoginResponse.builder()
                .loginStatus(LoginStatus.EXPIRED)
                .build(), HttpStatus.UNAUTHORIZED);
          } else {
            OffsetDateTime expiryDateTime = OffsetDateTime.now().plusMinutes(5);

            Token refreshToken = Token.builder()
                .token(UUID.randomUUID())
                .userId(user.userId())
                .expiryDateTime(expiryDateTime.plusMinutes(10))
                .build();

            tokenRepository.insertToken(refreshToken);

            return ResponseEntity.ok(LoginResponse.builder()
                .loginStatus(user.temporary() ? LoginStatus.TEMPORARY : LoginStatus.VALID)
                .accessToken(jwtService.generateAccessToken(user, expiryDateTime))
                .refreshToken(refreshToken.token().toString())
                .build());
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
  @GetMapping("refresh")
  public Mono<ResponseEntity<LoginResponse>> refresh(ServerHttpRequest request) {
    String authToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    return ReactiveSecurityContextHolder.getContext()
        .map(securityContext -> {
          return new ResponseEntity<>(LoginResponse.builder()
              .loginStatus(LoginStatus.EXPIRED)
              .build(), HttpStatus.UNAUTHORIZED
          );
        })
        .defaultIfEmpty(new ResponseEntity<>(LoginResponse.builder()
            .loginStatus(LoginStatus.INVALID)
            .build(), HttpStatus.UNAUTHORIZED)
        );

  }
}
