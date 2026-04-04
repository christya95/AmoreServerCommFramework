package amore.servercomm.registry;

import amore.servercomm.mailbox.MailboxSnapshot;
import amore.servercomm.mailbox.SeqlockMailbox;
import amore.servercomm.ring.TraceRingBuffer;
import amore.servercomm.tick.TraceRecord;

/** Preallocated per {@link com.hypixel.hytale.server.core.modules.entity.player.PlayerInput} (lazy on first packet). */
public final class PerPlayerTraceState {

    public static final int DEFAULT_RING_SIZE = 4096;

    public final SeqlockMailbox mailbox = new SeqlockMailbox();
    public final TraceRingBuffer ring = new TraceRingBuffer(DEFAULT_RING_SIZE);
    public final TraceRecord scratch = new TraceRecord();
    public final MailboxSnapshot mailboxSnap = new MailboxSnapshot();

    public long lastIngressNano;
}
