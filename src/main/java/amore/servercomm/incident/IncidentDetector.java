package amore.servercomm.incident;

import amore.servercomm.api.ApplyResult;
import amore.servercomm.tick.TraceRecord;
import com.hypixel.hytale.logger.HytaleLogger;

/** Rate-limited one-line incident summaries + optional auto-dump trigger. */
public final class IncidentDetector {

    public static final long DEFAULT_STARVATION_NS = 80_000_000L;

    private static long lastLogNs;

    private IncidentDetector() {}

    public static void maybeReport(
        HytaleLogger log,
        String username,
        TraceRecord r,
        Runnable autoDump
    ) {
        long now = System.nanoTime();
        if (now - lastLogNs < 50_000_000L) {
            return;
        }

        boolean jumpReject =
            r.jumpEdge
                && r.applyResult != ApplyResult.APPLIED
                && r.applyResult != ApplyResult.NONE
                && r.applyResult != ApplyResult.REJECT_NOT_REQUESTED;
        boolean conflict = r.sourceConflict;
        boolean starve = r.starvation;

        if (!jumpReject && !conflict && !starve) {
            return;
        }

        lastLogNs = now;
        String id = Long.toHexString(now);
        if (autoDump != null) {
            try {
                autoDump.run();
            } catch (Exception ignored) {
            }
        }
        ((HytaleLogger.Api) log.atInfo()).log(
            "[Amore incident %s] user=%s jumpEdge+reject=%b conflict=%b starvation=%b dumpId=%s",
            id,
            username,
            jumpReject,
            conflict,
            starve,
            id);
    }
}
