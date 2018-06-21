// Copyright (c) Cognitect, Inc.
// All rights reserved.
package com.cognitect.transit.impl;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.OptionalInt;
import java.util.function.Supplier;

/**
 * @author Michael Locher
 */
public interface BinaryProvider extends Supplier<InputStream> {

  static BinaryProvider create(final long size, final Supplier<InputStream> data) {
    return new BinaryProvider() {
      @Override
      public OptionalInt sizeIfKnown() {
        final int safeSize = size > MAX_VALUE ? MAX_VALUE : (int) size;
        return safeSize < 1 ? OptionalInt.empty() : OptionalInt.of(safeSize);
      }

      @Override
      public InputStream get() {
        return data.get();
      }
    };
  }

  default OptionalInt sizeIfKnown() {
    return OptionalInt.empty();
  }

  default String asBase64() {
    final long size = sizeIfKnown().orElse(32);
    final long base64size = ((4L * size / 3L) + 3L) & ~3L;
    final int safeBase64size = base64size > MAX_VALUE ? MAX_VALUE : (int) base64size;
    try (InputStream in = get()) {
      try (final ByteArrayOutputStream baos = new ByteArrayOutputStream(safeBase64size)) {
        try (OutputStream enc = Base64.getEncoder().wrap(baos)) {
          //NOTE: on Java 9+, could be replaced with InputStream.html#transferTo
          final byte[] buf = new byte[max(1, min(sizeIfKnown().orElse(4096), 4096))];
          while (true) {
            final int r = in.read(buf);
            if (r == -1) {
              break;
            }
            enc.write(buf, 0, r);
          }
          enc.close();
          return baos.toString(StandardCharsets.ISO_8859_1.name());
        }
      }
    } catch (final IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
