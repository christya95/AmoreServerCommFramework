package amore.servercomm.mailbox;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Seqlock-style mailbox: writers (input thread) bump sequence odd during write; readers (tick) copy when stable.
 * Multiple packet kinds share one lock-free cell block updated under a simple synchronized on this for clarity
 * (Hytale queue is likely single-threaded per player; if not, sync is still correct).
 */
public final class SeqlockMailbox {

    private final AtomicLong globalSeq = new AtomicLong();

    private volatile long lastAnyPacketNs;

    private volatile long smsNs;
    private volatile int smsSeq;
    private volatile int smsMovementBits;

    private volatile long cmNs;
    private volatile int cmSeq;
    private volatile int cmMovementBits;
    private volatile byte cmWishQ;
    private volatile byte cmVelYQ;

    private volatile long icNs;
    private volatile int icSeq;
    private volatile int icSummary;

    private volatile long miNs;
    private volatile int miSeq;
    private volatile long miClientTimestamp;

    public void recordSmsArrival(int packedMovementBits) {
        long ns = System.nanoTime();
        synchronized (this) {
            smsNs = ns;
            smsSeq++;
            smsMovementBits = packedMovementBits;
            lastAnyPacketNs = ns;
            globalSeq.incrementAndGet();
        }
    }

    public void recordClientMovement(int packedMs, byte wishQ, byte velYQ) {
        long ns = System.nanoTime();
        synchronized (this) {
            cmNs = ns;
            cmSeq++;
            cmMovementBits = packedMs;
            cmWishQ = wishQ;
            cmVelYQ = velYQ;
            lastAnyPacketNs = ns;
            globalSeq.incrementAndGet();
        }
    }

    public void recordInteractionChains(int typeSummary) {
        long ns = System.nanoTime();
        synchronized (this) {
            icNs = ns;
            icSeq++;
            icSummary = typeSummary;
            lastAnyPacketNs = ns;
            globalSeq.incrementAndGet();
        }
    }

    public void recordMouseInteraction(long clientTimestamp) {
        long ns = System.nanoTime();
        synchronized (this) {
            miNs = ns;
            miSeq++;
            miClientTimestamp = clientTimestamp;
            lastAnyPacketNs = ns;
            globalSeq.incrementAndGet();
        }
    }

    public long getLastAnyPacketNs() {
        return lastAnyPacketNs;
    }

    /** Snapshot into reusable struct (tick thread). */
    public void copyTo(MailboxSnapshot s) {
        synchronized (this) {
            s.lastAnyPacketNs = lastAnyPacketNs;
            s.smsNs = smsNs;
            s.smsSeq = smsSeq;
            s.smsMovementBits = smsMovementBits;
            s.cmNs = cmNs;
            s.cmSeq = cmSeq;
            s.cmMovementBits = cmMovementBits;
            s.cmWishQ = cmWishQ;
            s.cmVelYQ = cmVelYQ;
            s.icNs = icNs;
            s.icSeq = icSeq;
            s.icSummary = icSummary;
            s.miNs = miNs;
            s.miSeq = miSeq;
            s.miClientTimestamp = miClientTimestamp;
        }
    }
}
