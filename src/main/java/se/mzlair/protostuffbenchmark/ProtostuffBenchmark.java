/*
 * Copyright (c) 2005, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package se.mzlair.protostuffbenchmark;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.openjdk.jmh.annotations.Benchmark;

import se.mzlair.protostuffbenchmark.proto.Test;
import se.mzlair.protostuffbenchmark.proto.TestMessage;

import java.io.IOException;

import io.protostuff.ByteArrayInput;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufOutput;

public class ProtostuffBenchmark {

  private final static byte[] packed = concat(
      generatePackedInt32s(1, 500), generatePackedInt32s(2, 500));

  private final static byte[] repeated = concat(
      generateRepeatedInt32s(1, 500), generateRepeatedInt32s(2, 500));

  private static byte[] concat(final byte[] a, final byte[] b) {
    byte[] c = new byte[a.length + b.length];
    System.arraycopy(a, 0, c, 0, a.length);
    System.arraycopy(b, 0, c, a.length, b.length);
    return c;
  }

  private static byte[] generateRepeatedInt32s(final int field, final int n) {
    // Generate protobuf with packed repeated fields
    final ProtobufOutput output = new ProtobufOutput(
        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

    try {
      final int ints[] = new int[] { 3, 270, 86942 };

      for (int i = 0; i < n; ++i) {
        output.writeInt32(field, ints[i%ints.length], true);
      }

      return output.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static byte[] generatePackedInt32s(final int field, final int n)  {
    // Generate protobuf with packed repeated fields
    final ProtobufOutput output = new ProtobufOutput(
        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));

    try {
      final ByteArrayDataOutput packed = ByteStreams.newDataOutput();

      final byte ints[][] = new byte[][] {
          //03        // first element (varint 3)
          new byte[] { (byte)0x03 } ,
          //8E 02     // second element (varint 270)
          new byte[] { (byte)0x8E, (byte)0x02 },
          //9E A7 05  // third element (varint 86942)
          new byte[] { (byte)0x9E, (byte)0xA7, (byte)0x05 },
      };

      for (int i = 0; i < n; ++i) {
        packed.write(ints[i%ints.length]);
      }

      final byte[] packedBytes = packed.toByteArray();

      output.writeByteRange(false, field, packedBytes, 0, packedBytes.length, true);

      return output.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Benchmark
  public void testProtostuffPacked() throws IOException {
    TestMessage tm = new TestMessage();
    tm.mergeFrom(new ByteArrayInput(packed, false), tm);
  }


  @Benchmark
  public void testProtostuffUnpacked() throws IOException {
    TestMessage tm = new TestMessage();
    tm.mergeFrom(new ByteArrayInput(repeated, false), tm);
  }

  @Benchmark
  public void testProtobufPacked() throws IOException {
    Test.TestMessage.parseFrom(packed);
  }

  @Benchmark
  public void testProtobufUnpacked() throws IOException {
    Test.TestMessage.parseFrom(repeated);
  }
}
