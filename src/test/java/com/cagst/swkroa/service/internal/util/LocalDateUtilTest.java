/*
 *  Copyright 2017 Netsmart Technologies, Inc. All rights reserved.
 *  NETSMART PROPRIETARY/CONFIDENTIAL.
 */
package com.cagst.swkroa.service.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Test class for the {@link LocalDateUtil} class.
 *
 * @author Craig Gaskill
 */
class LocalDateUtilTest {
  /**
   * Test the convertFromDate method.
   */
  @Test
  void testConvertFromDate() {
    LocalDate localDate1 = LocalDateUtil.convertFromDate(null);
    assertNull(localDate1, "Ensure the local date is null.");

    Date dt = new Date(2017 - 1900, 2 - 1, 23);
    LocalDate localDate2 = LocalDateUtil.convertFromDate(dt);
    assertEquals("2017-02-23", localDate2.toString(), "Ensure the local date is correct.");
  }

  /**
   * Test the convertToDate method.
   */
  @Test
  void testConvertToDate() {
    Date dt1 = LocalDateUtil.convertToDate(null);
    assertNull(dt1, "Ensure the date is null.");

    LocalDate localDate = LocalDate.of(2017, 1, 23);
    Date dt2 = LocalDateUtil.convertToDate(localDate);
    assertEquals("2017-01-23", dt2.toString());
  }
}
