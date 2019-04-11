package com.cagst.swkroa.service.config;

import com.cagst.swkroa.service.internal.util.ObjectMapperUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for some Application components.
 *
 * @author Craig Gaskill
 */
@Configuration
public class ApplicationConfig {
  @Bean
  @Primary
  public ObjectMapper getObjectMapper() {
    return ObjectMapperUtil.getObjectMapper();
  }
}
