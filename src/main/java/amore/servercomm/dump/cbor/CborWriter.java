package amore.servercomm.dump.cbor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Minimal RFC 8949 CBOR encoder (unsigned int, text, bool, array/map headers). No floats, tags, or indefinite length.
 */
public final class CborWriter {

    private final ByteArrayOutputStream out = new ByteArrayOutputStream(4096);

    public void writeBool(boolean v) {
        out.write(v ? 0xf5 : 0xf4);
    }

    public void writeUnsigned(long v) throws IOException {
        if (v < 0) {
            throw new IllegalArgumentException("negative: " + v);
        }
        if (v < 24) {
            out.write((int) (0x00 | v));
        } else if (v <= 0xffL) {
            out.write(0x18);
            out.write((int) v);
        } else if (v <= 0xffffL) {
            out.write(0x19);
            out.write((int) (v >> 8));
            out.write((int) (v & 0xff));
        } else if (v <= 0xffffffffL) {
            out.write(0x1a);
            out.write((int) (v >> 24));
            out.write((int) ((v >> 16) & 0xff));
            out.write((int) ((v >> 8) & 0xff));
            out.write((int) (v & 0xff));
        } else {
            out.write(0x1b);
            for (int s = 56; s >= 0; s -= 8) {
                out.write((int) ((v >>> s) & 0xff));
            }
        }
    }

    public void writeSigned(long v) throws IOException {
        if (v >= 0) {
            writeUnsigned(v);
            return;
        }
        long n = -1L - v;
        if (n < 24) {
            out.write((int) (0x20 | n));
        } else if (n <= 0xffL) {
            out.write(0x38);
            out.write((int) n);
        } else if (n <= 0xffffL) {
            out.write(0x39);
            out.write((int) (n >> 8));
            out.write((int) (n & 0xff));
        } else if (n <= 0xffffffffL) {
            out.write(0x3a);
            out.write((int) (n >> 24));
            out.write((int) ((n >> 16) & 0xff));
            out.write((int) ((n >> 8) & 0xff));
            out.write((int) (n & 0xff));
        } else {
            out.write(0x3b);
            for (int s = 56; s >= 0; s -= 8) {
                out.write((int) ((n >>> s) & 0xff));
            }
        }
    }

    public void writeText(String s) throws IOException {
        if (s == null) {
            out.write(0x60);
            return;
        }
        byte[] utf8 = s.getBytes(StandardCharsets.UTF_8);
        int len = utf8.length;
        if (len < 24) {
            out.write(0x60 | len);
        } else if (len <= 0xff) {
            out.write(0x78);
            out.write(len);
        } else if (len <= 0xffff) {
            out.write(0x79);
            out.write(len >> 8);
            out.write(len & 0xff);
        } else {
            out.write(0x7a);
            out.write((len >> 24) & 0xff);
            out.write((len >> 16) & 0xff);
            out.write((len >> 8) & 0xff);
            out.write(len & 0xff);
        }
        out.writeBytes(utf8);
    }

    /** CBOR array of {@code n} items (major type 4). */
    public void writeArrayHeader(int n) throws IOException {
        if (n < 24) {
            out.write(0x80 | n);
        } else if (n <= 0xff) {
            out.write(0x98);
            out.write(n);
        } else if (n <= 0xffff) {
            out.write(0x99);
            out.write(n >> 8);
            out.write(n & 0xff);
        } else {
            out.write(0x9a);
            out.write((n >> 24) & 0xff);
            out.write((n >> 16) & 0xff);
            out.write((n >> 8) & 0xff);
            out.write(n & 0xff);
        }
    }

    /** CBOR map of {@code n} key-value pairs (major type 5). */
    public void writeMapHeader(int pairs) throws IOException {
        if (pairs < 24) {
            out.write(0xa0 | pairs);
        } else if (pairs <= 0xff) {
            out.write(0xb8);
            out.write(pairs);
        } else if (pairs <= 0xffff) {
            out.write(0xb9);
            out.write(pairs >> 8);
            out.write(pairs & 0xff);
        } else {
            out.write(0xba);
            out.write((pairs >> 24) & 0xff);
            out.write((pairs >> 16) & 0xff);
            out.write((pairs >> 8) & 0xff);
            out.write(pairs & 0xff);
        }
    }

    public byte[] toByteArray() {
        return out.toByteArray();
    }
}
