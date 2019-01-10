/*
 *  Copyright 2017 Netsmart Technologies, Inc. All rights reserved.
 *  NETSMART PROPRIETARY/CONFIDENTIAL.
 */
package com.cagst.swkroa.service.internal.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import javax.annotation.Nullable;

/**
 * Static convenience methods that help transform to / from a {@link LocalDateTime}.
 *
 * @author Craig Gaskill
 */
public abstract class LocalDateTimeUtil {
  /**
   * Will convert the specified {@link Timestamp} into a {@link LocalDateTime}.
   * <p>
   * NOTE: This is intended to be used as a convenience method for retrieving a {@link LocalDateTime} from the database.
   * </p>
   *
   * @param ts
   *   The {@link Timestamp} to convert, the time portion will be ignored.
   *
   * @return A {@link LocalDateTime} representation of the specified date, {@code null} if the specified date was null.
   */
  @Nullable
  public static LocalDateTime convertFromTimestamp(@Nullable Timestamp ts) {
    return (ts != null ? ts.toLocalDateTime() : null);
  }

  /**
   * Will convert the specified {@link LocalDateTime} into a {@link Timestamp} object.
   * <p>
   * NOTE: This is intended to be used as a convenience method for persisting a {@link LocalDateTime} to a database.
   * </p>
   *
   * @param ldt
   *   The {@link LocalDateTime} to convert.
   *
   * @return A {@link Timestamp} representation of the specified LocalDate, {@code null} if the specified LocalDate was null.
   */
  public static Timestamp convertToTimestamp(@Nullable LocalDateTime ldt) {
    return convertToTimestamp(ldt, OffsetDateTime.now().getOffset());
  }

  /**
   * Will convert the specified {@link LocalDateTime} into a {@link Timestamp} object.
   * <p>
   * NOTE: This is intended to be used as a convenience method for persisting a {@link LocalDateTime} to a database.
   * </p>
   *
   * @param ldt
   *   The {@link LocalDateTime} to convert.
   * @param zoneOffset
   *   The {@link ZoneOffset} to use to convert to a Timestamp.
   *
   * @return A {@link Timestamp} representation of the specified LocalDate, {@code null} if the specified LocalDate was null.
   */
  public static Timestamp convertToTimestamp(@Nullable LocalDateTime ldt, @Nullable ZoneOffset zoneOffset) {
    if (zoneOffset == null) {
      zoneOffset = OffsetDateTime.now().getOffset();
    }

    return (ldt != null ? Timestamp.from(ldt.toInstant(zoneOffset)) : null);
  }
}
