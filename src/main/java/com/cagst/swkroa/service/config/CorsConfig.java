package com.cagst.swkroa.service.config;

import javax.inject.Inject;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * Provides configuration for CORS.
 *
 * @author Craig Gaskill
 */
@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class CorsConfig {
  private final CorsProperties corsProperties;

  @Inject
  public CorsConfig(CorsProperties corsProperties) {
    this.corsProperties = corsProperties;
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsProperties.toCorsConfiguration());

    return source;
  }
}
