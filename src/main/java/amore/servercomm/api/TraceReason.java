package amore.servercomm.api;

/** Opaque reason bitmask for ability-specific tracing; JossDoubleJump uses bits in adapter. */
public final class TraceReason {
    public static final int OK = 0;
    public static final int MASK_POST_LIFTOFF = 1;
    public static final int SIGNAL_SOURCE_RAW = 1 << 4;
    public static final int SIGNAL_SOURCE_PENDING_MS = 1 << 5;
    public static final int SIGNAL_SOURCE_QUEUE_SMS_LIVE = 1 << 6;
    public static final int SIGNAL_SOURCE_QUEUE_SMS = 1 << 7;
    public static final int SIGNAL_SOURCE_FALLBACK = 1 << 8;

    private TraceReason() {}
}
