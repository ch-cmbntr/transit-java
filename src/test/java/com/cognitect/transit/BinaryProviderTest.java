// Copyright (c) Cognitect, Inc.
// All rights reserved.
package com.cognitect.transit;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Base64;

import com.cognitect.transit.impl.BinaryProvider;
import com.cognitect.transit.impl.Util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BinaryProviderTest extends TestCase {

  public BinaryProviderTest(final String testName) {
    super(testName);
  }

  public static Test suite() {
    return new TestSuite(BinaryProviderTest.class);
  }

  public void testBase64Encoding() {
    assertEncoding("", "");
    assertEncoding("MQ==", "1");
    assertEncoding("MTI=", "12");
    assertEncoding("MTIz", "123");
    assertEncoding("MTIzNA==", "1234");
    assertEncoding("MTIzNDU=", "12345");
    assertEncoding("MTIzNDU2", "123456");
    assertEncoding("aGVsbG8gd29ybGQ=", "hello world");
    assertEncoding("dW1sYXV0cyDDpMO2w7zDqQ==", "umlauts \u00E4\u00F6\u00FC\u00E9");
  }

  private static void assertEncoding(final String expected, final String msg) {
    final String reference = Base64.getEncoder().encodeToString(msg.getBytes(UTF_8));
    final String encoded = utf8(msg).asBase64();
    final String decoded = new String(Util.decodeBase64(encoded), UTF_8);

    assertEquals(expected, encoded);
    assertEquals(expected, reference);
    assertEquals(msg, decoded);
  }

  private static BinaryProvider utf8(final String s) {
    final ByteBuffer b = UTF_8.encode(s);
    return BinaryProvider.create(b.remaining(), ()-> {
      final ByteBuffer bb = b.duplicate();
      return new InputStream() {
        @Override
        public int read() {
          return bb.hasRemaining() ? bb.get() : -1;
        }
      };
    });
  }

}
