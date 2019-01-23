package com.cagst.swkroa.service.config;

import java.math.BigInteger;
import java.security.SecureRandom;

import com.cagst.swkroa.service.internal.util.ObjectMapperUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

  private static final int KEY_LENGTH = 32;

  @Bean("secretKey")
  public String getSecretKey() {
    SecureRandom random = new SecureRandom();
    BigInteger randomInt = new BigInteger(KEY_LENGTH * 5, random);

    String secretKey = randomInt.toString(32);
    LOGGER.info("Using secret key [{}]", secretKey);

    return secretKey;
  }

  @Bean
  @Primary
  public ObjectMapper getObjectMapper() {
    return ObjectMapperUtil.getObjectMapper();
  }
}
