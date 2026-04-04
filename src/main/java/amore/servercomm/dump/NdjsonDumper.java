package amore.servercomm.dump;

import amore.servercomm.tick.TraceRecord;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** NDJSON: one JSON object per line. */
public final class NdjsonDumper {

    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    private NdjsonDumper() {}

    public static void write(Path path, TraceRecord[] rows, int count) throws IOException {
        Files.createDirectories(path.getParent());
        try (BufferedWriter w = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            for (int i = 0; i < count; i++) {
                w.write(GSON.toJson(new TraceRecordJson(rows[i])));
                w.newLine();
            }
        }
    }

    /** Gson-friendly DTO (avoid enum issues). */
    private static final class TraceRecordJson {
        long tickId;
        long serverTimeNs;
        long rxSmsNs;
        long rxCmNs;
        long rxIcNs;
        long rxMiNs;
        int seqSms;
        int seqCm;
        int seqIc;
        int seqMi;
        int movementBits;
        int wishMoveQ;
        int velYQ;
        boolean jumpHeld;
        boolean jumpEdge;
        boolean releaseEdge;
        boolean onGround;
        boolean sourceConflict;
        boolean starvation;
        boolean postLiftoffMaskActive;
        int sourceTag;
        int fsmState;
        int queueSize;
        int queueHeadAgeTicks;
        int reasonCode;
        String applyResult;
        long extraJumpCount;
        long extraCooldownMsRemaining;
        long extraLiftoffDelta;

        TraceRecordJson(TraceRecord r) {
            this.tickId = r.tickId;
            this.serverTimeNs = r.serverTimeNs;
            this.rxSmsNs = r.rxSmsNs;
            this.rxCmNs = r.rxCmNs;
            this.rxIcNs = r.rxIcNs;
            this.rxMiNs = r.rxMiNs;
            this.seqSms = r.seqSms;
            this.seqCm = r.seqCm;
            this.seqIc = r.seqIc;
            this.seqMi = r.seqMi;
            this.movementBits = r.movementBits;
            this.wishMoveQ = r.wishMoveQ;
            this.velYQ = r.velYQ;
            this.jumpHeld = r.jumpHeld;
            this.jumpEdge = r.jumpEdge;
            this.releaseEdge = r.releaseEdge;
            this.onGround = r.onGround;
            this.sourceConflict = r.sourceConflict;
            this.starvation = r.starvation;
            this.postLiftoffMaskActive = r.postLiftoffMaskActive;
            this.sourceTag = r.sourceTag;
            this.fsmState = r.fsmState;
            this.queueSize = r.queueSize;
            this.queueHeadAgeTicks = r.queueHeadAgeTicks;
            this.reasonCode = r.reasonCode;
            this.applyResult = r.applyResult != null ? r.applyResult.name() : "NONE";
            this.extraJumpCount = r.extraJumpCount;
            this.extraCooldownMsRemaining = r.extraCooldownMsRemaining;
            this.extraLiftoffDelta = r.extraLiftoffDelta;
        }
    }
}
