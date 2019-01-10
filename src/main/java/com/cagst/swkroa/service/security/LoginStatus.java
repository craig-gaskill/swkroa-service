package com.cagst.swkroa.service.security;

/**
 * Defines possible statuses that can be returned upon login.
 *
 * @author Craig Gaskill
 */
public enum LoginStatus {
  DISABLED,
  LOCKED,
  EXPIRED,
  TEMPORARY,
  INVALID,
  VALID
}
