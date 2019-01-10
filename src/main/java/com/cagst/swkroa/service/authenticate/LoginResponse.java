package com.cagst.swkroa.service.authenticate;

import com.cagst.swkroa.service.security.LoginStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Accessors;

@JsonPropertyOrder({
    "status",
    "access",
    "refresh"
})
@Value
@Accessors(fluent = true)
@Builder(toBuilder = true)
public class LoginResponse {
  @JsonProperty("status")
  private LoginStatus loginStatus;

  @JsonProperty("access")
  private String accessToken;

  @JsonProperty("refresh")
  private String refreshToken;
}
