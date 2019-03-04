package com.cagst.swkroa.service.util;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Test class for the {@link UuidAdapter} class.
 *
 * @author Craig Gaskill
 */
@DisplayName("UuidAdapter")
class UuidAdapterTest {
  private UUID uuid = UUID.randomUUID();

  @Test
  @DisplayName("should return a 16 byte representation of a UUID")
  void testConvertToBinary() {
    byte[] result = UuidAdapter.convert(uuid);
    assertAll("Ensure the result",
        () -> assertNotNull(result, "is valid"),
        () -> assertEquals(16, result.length, "is 16 bytes long"));
  }

  @Test
  @DisplayName("should return a UUID representation of a 16 byte array")
  void testConvertToUuid() {
    byte[] bytes = UuidAdapter.convert(uuid);

    UUID result = UuidAdapter.convert(bytes);
    assertAll("Ensure the result",
        () -> assertNotNull(result, "is valid"),
        () -> assertEquals(uuid, result, "is the same UUID"));
  }
}
