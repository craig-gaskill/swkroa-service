package com.cagst.swkroa.service.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Time;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;

/**
 * Test class for the {@link LocalTimeUtil} class.
 *
 * @author Craig Gaskill
 */
class LocalTimeUtilTest {
  @Test
  void testConvertFromTime() {
    LocalTime localTime1 = LocalTimeUtil.convertFromTime(null);
    assertNull(localTime1, "Ensure the local time is correct.");

    Time time = new Time(13, 45, 43);
    LocalTime localTime2 = LocalTimeUtil.convertFromTime(time);
    assertEquals("13:45:43", localTime2.toString(), "Ensure the local time is correct.");
  }

  @Test
  void testConvertToTime() {
    Time time1 = LocalTimeUtil.convertToTime(null);
    assertNull(time1, "Ensure the time is correct.");

    LocalTime localTime = LocalTime.of(13, 45, 43);
    Time time2 = LocalTimeUtil.convertToTime(localTime);
    assertEquals("13:45:43", time2.toString(), "Ensure the time is correct.");
  }
}
