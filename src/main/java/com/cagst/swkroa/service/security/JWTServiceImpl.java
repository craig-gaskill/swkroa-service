package com.cagst.swkroa.service.security;

import java.time.OffsetDateTime;
import java.util.Date;
import javax.inject.Inject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cagst.swkroa.service.user.User;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

/**
 * Defines methods for generating an Access and a Refresh token.
 *
 * @author Craig Gaskill
 */
@Service
/* package */ class JWTServiceImpl implements JWTService {
  private static final Logger LOGGER = LoggerFactory.getLogger(JWTServiceImpl.class);

  private final Algorithm algorithm;
  private final JWTVerifier jwtVerifier;

  @Inject
  public JWTServiceImpl(@Qualifier("secretKey") String secretKey) {
    LOGGER.info("Generating Algorithm and Verifier using [{}]", secretKey);
    this.algorithm   = Algorithm.HMAC512(secretKey);
    this.jwtVerifier = JWT.require(algorithm).build();
  }

  @Override
  public String generateAccessToken(User user, OffsetDateTime expiryDateTime) {
    return JWT.create()
        .withSubject(String.valueOf(user.userId()))
        .withExpiresAt(Date.from(expiryDateTime.toInstant()))
        .sign(algorithm);
  }

  @Override
  public boolean isTokenValid(String token) {
    DecodedJWT decodedJWT = decodeToken(token);
    return (decodedJWT != null);
  }

  @Override
  public long getUserId(String token) {
    DecodedJWT decodedJWT = decodeToken(token);
    if (decodedJWT == null) {
      return 0;
    }

    String subject = decodedJWT.getSubject();
    if (StringUtils.isNumeric(subject)) {
      return Long.valueOf(subject);
    } else {
      return 0;
    }
  }

  @Override
  public String[] getUserRoles(String token) {
    return new String[]{"ROLE_ADMIN"};
  }

  @Nullable
  private DecodedJWT decodeToken(String token) {
    try {
      return jwtVerifier.verify(token);
    } catch (Exception ex) {
      LOGGER.error("Failed to decode token due to [{}]", ex.getMessage());
      return null;
    }
  }
}
