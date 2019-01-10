package com.cagst.swkroa.service.security;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Implements the {@link ServerSecurityContextRepository} to forward the JWT Token to the
 * AuthenticationManager.
 *
 * @author Craig Gaskill
 */
@Component
public class SecurityContextRepository implements ServerSecurityContextRepository {
  private static final String BEARER_TOKEN = "Bearer ";

  private final ReactiveAuthenticationManager authenticationManager;

  @Inject
  public SecurityContextRepository(ReactiveAuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }

  @Override
  public Mono<Void> save(ServerWebExchange serverWebExchange, SecurityContext securityContext) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Mono<SecurityContext> load(ServerWebExchange serverWebExchange) {
    ServerHttpRequest request = serverWebExchange.getRequest();
    String accessToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

    if (StringUtils.startsWith(accessToken, BEARER_TOKEN)) {
      String token = StringUtils.removeStart(accessToken, BEARER_TOKEN);
      Authentication auth = new UsernamePasswordAuthenticationToken(null, token);

      return authenticationManager.authenticate(auth).map(SecurityContextImpl::new);
    } else {
      return Mono.empty();
    }
  }
}
