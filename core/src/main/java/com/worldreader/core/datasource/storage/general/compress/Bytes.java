package com.worldreader.core.datasource.storage.general.compress;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class Bytes {

  /**
   * There are three methods to implement
   * {@link FileChannel#transferTo(long, long, WritableByteChannel)}:
   *
   * <ol>
   * <li>Use sendfile(2) or equivalent. Requires that both the input channel and the output channel
   *     have their own file descriptors. Generally this only happens when both channels are files
   *     or sockets. This performs zero copies - the bytes never enter userspace.
   * <li>Use mmap(2) or equivalent. Requires that either the input channel or the output channel
   *     have file descriptors. Bytes are copied from the file into a kernel buffer, then directly
   *     into the other buffer (userspace). Note that if the file is very large, a naive
   *     implementation will effectively put the whole file in memory. On many systems with paging
   *     and virtual memory, this is not a problem - because it is mapped read-only, the kernel can
   *     always page it to disk "for free". However, on systems where killing processes happens all
   *     the time in normal conditions (i.e., android) the OS must make a tradeoff between paging
   *     memory and killing other processes - so allocating a gigantic buffer and then sequentially
   *     accessing it could result in other processes dying. This is solvable via madvise(2), but
   *     that obviously doesn't exist in java.
   * <li>Ordinary copy. Kernel copies bytes into a kernel buffer, from a kernel buffer into a
   *     userspace buffer (byte[] or ByteBuffer), then copies them from that buffer into the
   *     destination channel.
   * </ol>
   *
   * This value is intended to be large enough to make the overhead of system calls negligible,
   * without being so large that it causes problems for systems with atypical memory management if
   * approaches 2 or 3 are used.
   */
  private static final int ZERO_COPY_CHUNK_SIZE = 512 * 1024;

  /**
   * Reads all bytes from an input stream into a byte array. Does not close the stream.
   *
   * @param in the input stream to read from
   * @return a byte array containing all the bytes from the stream
   * @throws IOException if an I/O error occurs
   */
  public static byte[] toByteArray(InputStream in) throws IOException {
    // Presize the ByteArrayOutputStream since we know how large it will need
    // to be, unless that value is less than the default ByteArrayOutputStream
    // size (32).
    ByteArrayOutputStream out = new ByteArrayOutputStream(Math.max(32, in.available()));
    copy(in, out);
    return out.toByteArray();
  }

  /**
   * Copies all bytes from the input stream to the output stream. Does not close or flush either
   * stream.
   *
   * @param from the input stream to read from
   * @param to the output stream to write to
   * @return the number of bytes copied
   * @throws IOException if an I/O error occurs
   */
  public static long copy(InputStream from, OutputStream to) throws IOException {
    //checkNotNull(from);
    //checkNotNull(to);
    byte[] buf = createBuffer();
    long total = 0;
    while (true) {
      int r = from.read(buf);
      if (r == -1) {
        break;
      }
      to.write(buf, 0, r);
      total += r;
    }
    return total;
  }

  /**
   * Creates a new byte array for buffering reads or writes.
   */
  static byte[] createBuffer() {
    return new byte[8192];
  }

}
