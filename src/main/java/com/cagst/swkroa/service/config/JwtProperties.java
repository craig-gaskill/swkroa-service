package com.cagst.swkroa.service.config;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Defines properties and their defaults for Security settings.
 *
 * @author Craig Gaskill
 */
@ConfigurationProperties(prefix = "swkroa.jwt")
public class JwtProperties {
  private static final Logger LOGGER = LoggerFactory.getLogger(JwtProperties.class);

  private static final int KEY_LENGTH = 32;

  private String securityKey;

  public void setSecurityKey(String key) {
    this.securityKey = key;
  }

  public String getSecurityKey() {
    if (StringUtils.isEmpty(securityKey)) {
      SecureRandom random = new SecureRandom();
      BigInteger randomInt = new BigInteger(KEY_LENGTH * 5, random);

      securityKey = randomInt.toString(32);
    }

    LOGGER.info("Using secret key [{}]", securityKey);
    return securityKey;
  }
}
