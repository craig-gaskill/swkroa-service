/*
 *  Copyright 2017 Netsmart Technologies, Inc. All rights reserved.
 *  NETSMART PROPRIETARY/CONFIDENTIAL.
 */
package com.cagst.swkroa.service.internal.util;

import java.sql.Time;
import java.time.LocalTime;

import org.springframework.lang.Nullable;

/**
 * Static convenience methods that help transform to / from a {@link LocalTime}.
 *
 * @author Craig Gaskill
 */
public abstract class LocalTimeUtil {
  /**
   * Will convert the specified {@link Time} into a {@link LocalTime}.
   * <p>
   * NOTE: This is intended to be used as a convenience method for retrieving a {@link LocalTime} from the database.
   * </p>
   *
   * @param time
   *     The {@link Time} to convert.
   *
   * @return A {@link LocalTime} representation of the specified timestamp, {@code null} if the specified object
   * was empty.
   */
  @Nullable
  public static LocalTime convertFromTime(@Nullable Time time) {
    return (time != null ? time.toLocalTime() : null);
  }

  /**
   * Will convert the specified {@link LocalTime} into a {@link Time}.
   * <p>
   * NOTE: This is intended to be used as a convenience method for persisting a {@link LocalTime} to the database.
   * </p>
   *
   * @param localTime
   *     The {@link LocalTime} to convert.
   *
   * @return A {@link LocalTime} representation of the specified timestamp, {@code null} if the specified object
   * was empty.
   */
  @Nullable
  public static Time convertToTime(@Nullable LocalTime localTime) {
    if (localTime == null) {
      return null;
    }

    return Time.valueOf(localTime);
  }
}
