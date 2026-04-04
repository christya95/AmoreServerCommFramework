package amore.servercomm.dump;

import amore.servercomm.tick.TraceRecord;
import java.io.IOException;
import java.nio.file.Path;

/** CBOR export — array of maps per tick (RFC 8949, no external library). */
public final class CborDumpHook {

    private CborDumpHook() {}

    public static void writeCbor(Path path, TraceRecord[] rows, int count) throws IOException {
        CborTraceDumper.write(path, rows, count);
    }
}
