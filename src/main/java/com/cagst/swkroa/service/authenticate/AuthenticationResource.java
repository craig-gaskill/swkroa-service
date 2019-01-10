package com.cagst.swkroa.service.authenticate;

import java.net.InetSocketAddress;
import javax.inject.Inject;

import com.cagst.swkroa.service.security.JWTService;
import com.cagst.swkroa.service.security.LoginStatus;
import com.cagst.swkroa.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
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
  private final JWTService jwtService;

  @Inject
  public AuthenticationResource(UserService userService,
                                JWTService jwtService
  ) {
    this.userService = userService;
    this.jwtService  = jwtService;
  }

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
            return ResponseEntity.ok(LoginResponse.builder()
                .loginStatus(user.temporary() ? LoginStatus.TEMPORARY : LoginStatus.VALID)
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .build());
          }
        })
        .defaultIfEmpty(new ResponseEntity<>(LoginResponse.builder()
            .loginStatus(LoginStatus.INVALID)
            .build(), HttpStatus.UNAUTHORIZED));
  }
}
