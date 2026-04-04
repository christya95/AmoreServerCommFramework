package amore.servercomm.dump;

import amore.servercomm.dump.cbor.CborWriter;
import amore.servercomm.tick.TraceRecord;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/** Writes an array of CBOR maps (same logical fields as {@link NdjsonDumper}). */
public final class CborTraceDumper {

    private CborTraceDumper() {}

    public static void write(Path path, TraceRecord[] rows, int count) throws IOException {
        Files.createDirectories(path.getParent());
        CborWriter w = new CborWriter();
        w.writeArrayHeader(count);
        for (int i = 0; i < count; i++) {
            writeRecord(w, rows[i]);
        }
        Files.write(path, w.toByteArray());
    }

    private static void writeRecord(CborWriter w, TraceRecord r) throws IOException {
        w.writeMapHeader(29);
        w.writeText("tickId");
        w.writeUnsigned(r.tickId);
        w.writeText("serverTimeNs");
        w.writeUnsigned(r.serverTimeNs);
        w.writeText("rxSmsNs");
        w.writeUnsigned(r.rxSmsNs);
        w.writeText("rxCmNs");
        w.writeUnsigned(r.rxCmNs);
        w.writeText("rxIcNs");
        w.writeUnsigned(r.rxIcNs);
        w.writeText("rxMiNs");
        w.writeUnsigned(r.rxMiNs);
        w.writeText("seqSms");
        w.writeUnsigned(r.seqSms & 0xffffffffL);
        w.writeText("seqCm");
        w.writeUnsigned(r.seqCm & 0xffffffffL);
        w.writeText("seqIc");
        w.writeUnsigned(r.seqIc & 0xffffffffL);
        w.writeText("seqMi");
        w.writeUnsigned(r.seqMi & 0xffffffffL);
        w.writeText("movementBits");
        w.writeUnsigned(r.movementBits & 0xffffffffL);
        w.writeText("wishMoveQ");
        w.writeUnsigned(r.wishMoveQ & 0xff);
        w.writeText("velYQ");
        w.writeUnsigned(r.velYQ & 0xff);
        w.writeText("jumpHeld");
        w.writeBool(r.jumpHeld);
        w.writeText("jumpEdge");
        w.writeBool(r.jumpEdge);
        w.writeText("releaseEdge");
        w.writeBool(r.releaseEdge);
        w.writeText("onGround");
        w.writeBool(r.onGround);
        w.writeText("sourceConflict");
        w.writeBool(r.sourceConflict);
        w.writeText("starvation");
        w.writeBool(r.starvation);
        w.writeText("postLiftoffMaskActive");
        w.writeBool(r.postLiftoffMaskActive);
        w.writeText("sourceTag");
        w.writeUnsigned(r.sourceTag & 0xff);
        w.writeText("fsmState");
        w.writeUnsigned(r.fsmState & 0xff);
        w.writeText("queueSize");
        w.writeUnsigned(r.queueSize & 0xffffffffL);
        w.writeText("queueHeadAgeTicks");
        w.writeUnsigned(r.queueHeadAgeTicks & 0xffffffffL);
        w.writeText("reasonCode");
        w.writeSigned(r.reasonCode);
        w.writeText("applyResult");
        w.writeText(r.applyResult != null ? r.applyResult.name() : "NONE");
        w.writeText("extraJumpCount");
        w.writeUnsigned(r.extraJumpCount);
        w.writeText("extraCooldownMsRemaining");
        w.writeUnsigned(r.extraCooldownMsRemaining);
        w.writeText("extraLiftoffDelta");
        w.writeSigned(r.extraLiftoffDelta);
    }
}
