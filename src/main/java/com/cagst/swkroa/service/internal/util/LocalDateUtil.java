/*
 *  Copyright 2017 Netsmart Technologies, Inc. All rights reserved.
 *  NETSMART PROPRIETARY/CONFIDENTIAL.
 */
package com.cagst.swkroa.service.internal.util;

import java.sql.Date;
import java.time.LocalDate;
import javax.annotation.Nullable;

/**
 * Static convenience methods that help transform to / from a {@link LocalDate}.
 *
 * @author Craig Gaskill
 */
public abstract class LocalDateUtil {
  /**
   * Will convert the specified {@link Date} into a {@link LocalDate}.
   * <p>
   * NOTE: This is intended to be used as a convenience method for retrieving a {@link LocalDate} from the database.
   * </p>
   *
   * @param dt
   *     The {@link Date} to convert, the time portion will be ignored.
   *
   * @return A {@link LocalDate} representation of the specified date, {@code null} if the specified date was null.
   */
  @Nullable
  public static LocalDate convertFromDate(@Nullable Date dt) {
    return (dt != null ? dt.toLocalDate() : null);
  }

  /**
   * Will convert the specified {@link LocalDate} into a {@link Date} object.
   * <p>
   * NOTE: This is intended to be used as a convenience method for persisting a {@link LocalDate} to a database.
   * </p>
   *
   * @param localDate
   *     The {@link LocalDate} to convert.
   *
   * @return A {@link Date} representation of the specified LocalDate, {@code null} if the specified LocalDate was null.
   */
  public static Date convertToDate(@Nullable LocalDate localDate) {
    if (localDate == null) {
      return null;
    }

    return Date.valueOf(localDate);
  }
}
