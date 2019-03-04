package com.cagst.swkroa.service.util;

import java.nio.ByteBuffer;
import java.util.UUID;

/**
 * Utility / adapter class to convert a UUID into a 16 byte array and vice-versa.
 *
 * @author Craig Gaskill
 */
public class UuidAdapter {
  /**
   * Will convert a {@link UUID} into a 16 byte array.
   *
   * @param uuid
   *    The {@link UUID} to convert.
   *
   * @return A 16 byte array representation of the specified UUID.
   */
  public static byte[] convert(UUID uuid) {
    ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
    buffer.putLong(uuid.getMostSignificantBits());
    buffer.putLong(uuid.getLeastSignificantBits());

    return buffer.array();
  }

  /**
   * Will convert a 16 byte array into a {@link UUID}.
   *
   * @param uuid
   *    The 16 byte array to convert.
   *
   * @return A {@link UUID} representation of the specified 16 byte array.
   */
  public static UUID convert(byte[] uuid) {
    ByteBuffer buffer = ByteBuffer.wrap(uuid);
    long high = buffer.getLong();
    long low  = buffer.getLong();

    return new UUID(high, low);
  }
}
