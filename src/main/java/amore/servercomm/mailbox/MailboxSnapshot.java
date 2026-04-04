package amore.servercomm.mailbox;

/** Read-only snapshot copied from {@link SeqlockMailbox} once per tick. */
public final class MailboxSnapshot {
    public long lastAnyPacketNs;

    public long smsNs;
    public int smsSeq;
    public int smsMovementBits;

    public long cmNs;
    public int cmSeq;
    public int cmMovementBits;
    public byte cmWishQ;
    public byte cmVelYQ;

    public long icNs;
    public int icSeq;
    public int icSummary;

    public long miNs;
    public int miSeq;
    public long miClientTimestamp;
}
