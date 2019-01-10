package com.cagst.swkroa.service.internal.jdbc;

/**
 * Defines the supported database dialects.
 *
 * @author Craig Gaskill
 */
public enum StatementDialect {
  HSQLDB, MYSQL, ORACLE, POSTGRESQL, SQLSERVER;

  public boolean needColumnName() {
    return (this == ORACLE || this == POSTGRESQL);
  }
}
