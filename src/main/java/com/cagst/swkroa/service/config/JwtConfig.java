package com.cagst.swkroa.service.config;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

/**
 * Provides configuration for JWT.
 *
 * @author Craig Gaskill
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {
  private final JwtProperties jwtProperties;

  @Inject
  public JwtConfig(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
  }

  @Bean
  public Algorithm getAlgorithm() {
    return Algorithm.HMAC512(jwtProperties.getSecurityKey());
  }
}
