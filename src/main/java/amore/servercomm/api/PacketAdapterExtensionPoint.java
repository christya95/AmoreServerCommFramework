package amore.servercomm.api;

import amore.servercomm.capture.WirePacketIngress;
import amore.servercomm.mailbox.SeqlockMailbox;

/**
 * Documentation hub for wire-level capture (no automatic registration against Hytale internals — APIs change between
 * server builds).
 *
 * <p><strong>Recommended path (JossDoubleJump):</strong> Hyxin mixin on {@code PlayerInput.queue} calls
 * {@link amore.servercomm.capture.InputQueueIngress#record(SeqlockMailbox, com.hypixel.hytale.server.core.modules.entity.player.PlayerInput.InputUpdate)}
 * via {@link ServerComm#stateFor(com.hypixel.hytale.server.core.modules.entity.player.PlayerInput)}.
 *
 * <p><strong>PacketAdapter path:</strong> When your adapter receives decoded movement packets, forward into the same
 * {@link SeqlockMailbox} using {@link WirePacketIngress#onSetMovementStates(SeqlockMailbox,
 * com.hypixel.hytale.protocol.MovementStates)} or {@link WirePacketIngress#onInputUpdate(SeqlockMailbox,
 * com.hypixel.hytale.server.core.modules.entity.player.PlayerInput.InputUpdate)} so tick-time traces stay aligned with
 * wire arrivals.
 */
public final class PacketAdapterExtensionPoint {

    private PacketAdapterExtensionPoint() {}
}
