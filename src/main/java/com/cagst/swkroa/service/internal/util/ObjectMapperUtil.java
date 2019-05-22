package com.cagst.swkroa.service.internal.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Static convenience methods for working with an {@link ObjectMapper}.
 *
 * @author Craig Gaskill
 */
public abstract class ObjectMapperUtil {
  private static ObjectMapper objectMapper;

  /**
   * Will return the {@link ObjectMapper} to be used.
   *
   * @return The {@link ObjectMapper} to be used.
   */
  public static ObjectMapper getObjectMapper() {
    if (objectMapper == null) {
      SimpleModule module = new SimpleModule();

      objectMapper = new ObjectMapper();
      objectMapper.registerModule(new JavaTimeModule());
//      objectMapper.registerModule(new AfterburnerModule());
      objectMapper.registerModule(module);
      objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
      objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    return objectMapper;
  }
}
