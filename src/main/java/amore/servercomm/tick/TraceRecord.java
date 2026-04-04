package amore.servercomm.tick;

import amore.servercomm.api.ApplyResult;

/**
 * One row per server tick (written by the tick path only). Reused in-place; no per-tick heap churn when used with
 * {@link amore.servercomm.ring.TraceRingBuffer}.
 */
public final class TraceRecord {
    public long tickId;
    public long serverTimeNs;

    public long rxSmsNs;
    public long rxCmNs;
    public long rxIcNs;
    public long rxMiNs;

    public int seqSms;
    public int seqCm;
    public int seqIc;
    public int seqMi;

    /** Packed {@link com.hypixel.hytale.protocol.MovementStates} bits (jumping, onGround, …). */
    public int movementBits;
    /** Quantized wish movement (e.g. octant or 0–8). */
    public byte wishMoveQ;
    /** Vertical velocity bucket or sign nibble. */
    public byte velYQ;

    public boolean jumpHeld;
    public boolean jumpEdge;
    public boolean releaseEdge;
    public boolean onGround;
    public boolean sourceConflict;
    public boolean starvation;
    public boolean postLiftoffMaskActive;

    /** Arbitrary tag (e.g. signal source ordinal). */
    public byte sourceTag;

    /** Ability FSM snapshot (adapter-defined encoding). */
    public byte fsmState;

    public int queueSize;
    public int queueHeadAgeTicks;

    public int reasonCode;
    public ApplyResult applyResult = ApplyResult.NONE;

    public long extraJumpCount;
    public long extraCooldownMsRemaining;
    public long extraLiftoffDelta;

    public void clear() {
        tickId = 0L;
        serverTimeNs = 0L;
        rxSmsNs = rxCmNs = rxIcNs = rxMiNs = 0L;
        seqSms = seqCm = seqIc = seqMi = 0;
        movementBits = 0;
        wishMoveQ = 0;
        velYQ = 0;
        jumpHeld = jumpEdge = releaseEdge = onGround = false;
        sourceConflict = starvation = postLiftoffMaskActive = false;
        sourceTag = 0;
        fsmState = 0;
        queueSize = queueHeadAgeTicks = 0;
        reasonCode = 0;
        applyResult = ApplyResult.NONE;
        extraJumpCount = extraCooldownMsRemaining = extraLiftoffDelta = 0L;
    }

    public void copyFrom(TraceRecord o) {
        if (o == null) {
            clear();
            return;
        }
        tickId = o.tickId;
        serverTimeNs = o.serverTimeNs;
        rxSmsNs = o.rxSmsNs;
        rxCmNs = o.rxCmNs;
        rxIcNs = o.rxIcNs;
        rxMiNs = o.rxMiNs;
        seqSms = o.seqSms;
        seqCm = o.seqCm;
        seqIc = o.seqIc;
        seqMi = o.seqMi;
        movementBits = o.movementBits;
        wishMoveQ = o.wishMoveQ;
        velYQ = o.velYQ;
        jumpHeld = o.jumpHeld;
        jumpEdge = o.jumpEdge;
        releaseEdge = o.releaseEdge;
        onGround = o.onGround;
        sourceConflict = o.sourceConflict;
        starvation = o.starvation;
        postLiftoffMaskActive = o.postLiftoffMaskActive;
        sourceTag = o.sourceTag;
        fsmState = o.fsmState;
        queueSize = o.queueSize;
        queueHeadAgeTicks = o.queueHeadAgeTicks;
        reasonCode = o.reasonCode;
        applyResult = o.applyResult;
        extraJumpCount = o.extraJumpCount;
        extraCooldownMsRemaining = o.extraCooldownMsRemaining;
        extraLiftoffDelta = o.extraLiftoffDelta;
    }
}
