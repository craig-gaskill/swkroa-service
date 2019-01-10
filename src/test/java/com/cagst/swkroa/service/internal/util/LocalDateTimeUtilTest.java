package com.cagst.swkroa.service.internal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test class for the {@link LocalDateTime} class.
 *
 * @author Craig Gaskill
 */
class LocalDateTimeUtilTest {
  @Test
  void testConvertFromTimestamp() {
    LocalDateTime ldt1 = LocalDateTimeUtil.convertFromTimestamp(null);
    assertNull(ldt1, "Ensure the local date time is null.");

    Timestamp ts = Timestamp.valueOf("2018-01-23 13:45:00");
    LocalDateTime ldt2 = LocalDateTimeUtil.convertFromTimestamp(ts);
    assertEquals("2018-01-23T13:45", ldt2.toString());
  }

  @Test
  @Disabled
  void testConvertToTimestamp() {
    Timestamp ts1 = LocalDateTimeUtil.convertToTimestamp(null);
    assertNull(ts1, "Ensure the timestamp is null.");

    LocalDateTime ldt = LocalDateTime.of(2018, 1, 23, 14, 45);
    Timestamp ts2 = LocalDateTimeUtil.convertToTimestamp(ldt);
    assertEquals("2018-01-23 13:45:00.0", ts2.toString());

    Timestamp ts3 = LocalDateTimeUtil.convertToTimestamp(ldt, null);
    assertEquals("2018-01-23 13:45:00.0", ts3.toString());
  }
}
