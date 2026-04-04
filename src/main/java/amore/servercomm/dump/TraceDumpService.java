package amore.servercomm.dump;

import amore.servercomm.registry.PerPlayerTraceState;
import amore.servercomm.tick.TraceRecord;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class TraceDumpService {

    public static final String DEFAULT_REL_DIR = "mods/amore-traces";

    private TraceDumpService() {}

    public static Path dumpNdjson(PerPlayerTraceState state, String username, int seconds) throws IOException {
        TraceRecord[] buf = allocBuf(state.ring.capacity());
        int keep = copyRecentSeconds(state, seconds, buf);
        String name = "amore-" + sanitize(username) + "-" + System.currentTimeMillis() + ".ndjson";
        Path path = Paths.get(DEFAULT_REL_DIR).resolve(name);
        NdjsonDumper.write(path, buf, keep);
        return path;
    }

    public static Path dumpPerfetto(PerPlayerTraceState state, String username, int seconds) throws IOException {
        TraceRecord[] buf = allocBuf(state.ring.capacity());
        int keep = copyRecentSeconds(state, seconds, buf);
        String name = "amore-" + sanitize(username) + "-" + System.currentTimeMillis() + "-trace.json";
        Path path = Paths.get(DEFAULT_REL_DIR).resolve(name);
        PerfettoTraceDumper.write(path, buf, keep);
        return path;
    }

    public static Path dumpCbor(PerPlayerTraceState state, String username, int seconds) throws IOException {
        TraceRecord[] buf = allocBuf(state.ring.capacity());
        int keep = copyRecentSeconds(state, seconds, buf);
        String name = "amore-" + sanitize(username) + "-" + System.currentTimeMillis() + ".cbor";
        Path path = Paths.get(DEFAULT_REL_DIR).resolve(name);
        CborDumpHook.writeCbor(path, buf, keep);
        return path;
    }

    private static TraceRecord[] allocBuf(int cap) {
        TraceRecord[] buf = new TraceRecord[cap];
        for (int i = 0; i < cap; i++) {
            buf[i] = new TraceRecord();
        }
        return buf;
    }

    /** Rows whose {@link TraceRecord#serverTimeNs} is within the last {@code seconds} (chronological segment of ring copy). */
    private static int copyRecentSeconds(PerPlayerTraceState state, int seconds, TraceRecord[] buf) {
        long cutoff = System.nanoTime() - Math.max(1, seconds) * 1_000_000_000L;
        int cap = state.ring.capacity();
        int n = state.ring.copyLast(cap, buf);
        int keep = 0;
        for (int i = 0; i < n; i++) {
            if (buf[i].serverTimeNs >= cutoff) {
                if (keep != i) {
                    buf[keep].copyFrom(buf[i]);
                }
                keep++;
            }
        }
        return keep;
    }

    private static String sanitize(String u) {
        if (u == null) {
            return "unknown";
        }
        return u.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
