package amore.servercomm.capture;

import amore.servercomm.mailbox.SeqlockMailbox;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
import javax.annotation.Nullable;

/**
 * Entry points for <strong>PacketAdapter</strong> / wire-level integrators. Call these when your inbound adapter decodes
 * movement-related packets, in addition to (or instead of) relying on the {@code PlayerInput.queue} mixin path.
 *
 * <p>Typical use: obtain {@link amore.servercomm.registry.PerPlayerTraceState#mailbox} via
 * {@link amore.servercomm.api.ServerComm#stateFor(PlayerInput)} for the same player entity.
 */
public final class WirePacketIngress {

    private WirePacketIngress() {}

    /** After decoding {@link MovementStates} from SetMovementStates (or equivalent). */
    public static void onSetMovementStates(@Nullable SeqlockMailbox mailbox, @Nullable MovementStates states) {
        if (mailbox == null || states == null) {
            return;
        }
        mailbox.recordSmsArrival(MovementBits.pack(states));
    }

    /**
     * When you already have a {@link PlayerInput.InputUpdate} from the protocol (same shape as the movement queue uses).
     */
    public static void onInputUpdate(@Nullable SeqlockMailbox mailbox, @Nullable PlayerInput.InputUpdate update) {
        InputQueueIngress.record(mailbox, update);
    }
}
