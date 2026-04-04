package amore.servercomm.ring;

import amore.servercomm.tick.TraceRecord;

/** Fixed-size power-of-two ring; preallocated {@link TraceRecord} slots. */
public final class TraceRingBuffer {

    private final TraceRecord[] slots;
    private final int mask;
    private long writeSeq;

    public TraceRingBuffer(int powerOfTwoSize) {
        if (powerOfTwoSize < 8 || Integer.bitCount(powerOfTwoSize) != 1) {
            throw new IllegalArgumentException("size must be a power of two >= 8");
        }
        this.mask = powerOfTwoSize - 1;
        this.slots = new TraceRecord[powerOfTwoSize];
        for (int i = 0; i < powerOfTwoSize; i++) {
            slots[i] = new TraceRecord();
        }
    }

    public int capacity() {
        return slots.length;
    }

    public long totalWrites() {
        return writeSeq;
    }

    /** Next writable slot (reused object). */
    public TraceRecord nextWritable() {
        long w = writeSeq++;
        int idx = (int) (w & mask);
        TraceRecord r = slots[idx];
        r.clear();
        return r;
    }

    /** Copy last {@code n} records in chronological order into {@code dest}. Returns count written. */
    public int copyLast(int n, TraceRecord[] dest) {
        if (n <= 0 || dest.length == 0 || writeSeq == 0) {
            return 0;
        }
        int cap = slots.length;
        int take = Math.min(Math.min(n, cap), dest.length);
        long newest = writeSeq - 1;
        long oldestSeq = newest - (take - 1);
        int out = 0;
        for (long seq = oldestSeq; seq <= newest; seq++) {
            int idx = (int) (seq & mask);
            dest[out].copyFrom(slots[idx]);
            out++;
        }
        return out;
    }
}
