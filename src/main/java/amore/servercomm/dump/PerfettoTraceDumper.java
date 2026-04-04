package amore.servercomm.dump;

import amore.servercomm.tick.TraceRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chrome trace event format (compatible with Perfetto JSON import): {@code { "traceEvents": [ ... ] }}.
 * Microsecond timestamps derived from {@link TraceRecord#serverTimeNs}; {@code args} mirrors NDJSON/CBOR fields.
 */
public final class PerfettoTraceDumper {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private PerfettoTraceDumper() {}

    public static void write(Path path, TraceRecord[] rows, int count) throws IOException {
        Files.createDirectories(path.getParent());
        List<Map<String, Object>> events = new ArrayList<>();
        long pid = 1;
        long tid = 1;
        for (int i = 0; i < count; i++) {
            TraceRecord r = rows[i];
            double tsUs = r.serverTimeNs / 1000.0;
            Map<String, Object> ev = new HashMap<>();
            ev.put("name", "amore.tick");
            ev.put("ph", "X");
            ev.put("ts", tsUs);
            ev.put("dur", 1.0);
            ev.put("pid", pid);
            ev.put("tid", tid);
            ev.put("args", fullArgs(r));
            events.add(ev);
        }
        Map<String, Object> root = new HashMap<>();
        root.put("traceEvents", events);
        try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            w.write(GSON.toJson(root));
        }
    }

    private static Map<String, Object> fullArgs(TraceRecord r) {
        Map<String, Object> args = new HashMap<>();
        args.put("tickId", r.tickId);
        args.put("serverTimeNs", r.serverTimeNs);
        args.put("rxSmsNs", r.rxSmsNs);
        args.put("rxCmNs", r.rxCmNs);
        args.put("rxIcNs", r.rxIcNs);
        args.put("rxMiNs", r.rxMiNs);
        args.put("seqSms", r.seqSms);
        args.put("seqCm", r.seqCm);
        args.put("seqIc", r.seqIc);
        args.put("seqMi", r.seqMi);
        args.put("movementBits", r.movementBits);
        args.put("wishMoveQ", r.wishMoveQ & 0xff);
        args.put("velYQ", r.velYQ & 0xff);
        args.put("jumpHeld", r.jumpHeld);
        args.put("jumpEdge", r.jumpEdge);
        args.put("releaseEdge", r.releaseEdge);
        args.put("onGround", r.onGround);
        args.put("sourceConflict", r.sourceConflict);
        args.put("starvation", r.starvation);
        args.put("postLiftoffMaskActive", r.postLiftoffMaskActive);
        args.put("sourceTag", r.sourceTag & 0xff);
        args.put("fsmState", r.fsmState & 0xff);
        args.put("queueSize", r.queueSize);
        args.put("queueHeadAgeTicks", r.queueHeadAgeTicks);
        args.put("reasonCode", r.reasonCode);
        args.put("applyResult", r.applyResult != null ? r.applyResult.name() : "NONE");
        args.put("extraJumpCount", r.extraJumpCount);
        args.put("extraCooldownMsRemaining", r.extraCooldownMsRemaining);
        args.put("extraLiftoffDelta", r.extraLiftoffDelta);
        return args;
    }
}
