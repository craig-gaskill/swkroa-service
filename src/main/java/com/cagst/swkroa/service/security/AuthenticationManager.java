package com.cagst.swkroa.service.security;

import java.util.Arrays;
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Implementation of {@link ReactiveAuthenticationManager} to validate a JWT token
 * from the Authorization header.
 *
 * @author Craig Gaskill
 */
@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {
  private final JWTService jwtService;

  @Inject
  public AuthenticationManager(JWTService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  public Mono<Authentication> authenticate(Authentication authentication) {
    String accessToken = authentication.getCredentials().toString();
    if (StringUtils.isEmpty(accessToken)) {
      return Mono.empty();
    }

    if (!jwtService.isTokenValid(accessToken)) {
      return Mono.empty();
    }

    long userId = jwtService.getUserId(accessToken);
    String[] roles = jwtService.getUserRoles(accessToken);

    if (userId == 0) {
      return Mono.empty();
    }

    return Mono.just(new UsernamePasswordAuthenticationToken(
        String.valueOf(userId),
        null,
        Arrays.stream(roles).map(SimpleGrantedAuthority::new).collect(Collectors.toList()))
    );
  }
}
