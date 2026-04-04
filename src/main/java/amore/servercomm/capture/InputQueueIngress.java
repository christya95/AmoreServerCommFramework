package amore.servercomm.capture;

import amore.servercomm.mailbox.SeqlockMailbox;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Called from a mixin at {@link PlayerInput#queue(PlayerInput.InputUpdate)} HEAD — same ingress as movement processing.
 * Optional PacketAdapter wiring can call the same helpers when available.
 */
public final class InputQueueIngress {

    private static final ConcurrentHashMap<Class<?>, Optional<Method>> MOVEMENT_STATES_GETTERS = new ConcurrentHashMap<>();

    private InputQueueIngress() {}

    public static void record(SeqlockMailbox mailbox, PlayerInput.InputUpdate update) {
        if (mailbox == null || update == null) {
            return;
        }
        if (update instanceof PlayerInput.SetMovementStates sms) {
            mailbox.recordSmsArrival(MovementBits.pack(sms.movementStates()));
            return;
        }
        String simple = update.getClass().getSimpleName();
        switch (simple) {
            case "ClientMovement" -> recordClientMovement(mailbox, update);
            case "SyncInteractionChains" -> mailbox.recordInteractionChains(hashInteractionTypes(update));
            case "MouseInteraction" -> mailbox.recordMouseInteraction(extractClientTimestamp(update));
            default -> {
                // Unknown update: still advances "last packet" notion via subclass-specific path if needed
            }
        }
    }

    private static void recordClientMovement(SeqlockMailbox mailbox, PlayerInput.InputUpdate update) {
        MovementStates ms = extractMovementStates(update);
        int bits = MovementBits.pack(ms);
        byte wish = quantizeWish(update);
        byte vy = quantizeVelY(update);
        mailbox.recordClientMovement(bits, wish, vy);
    }

    private static MovementStates extractMovementStates(Object update) {
        Optional<Method> om =
            MOVEMENT_STATES_GETTERS.computeIfAbsent(
                update.getClass(), c -> Optional.ofNullable(findMovementStatesGetter(c)));
        Method m = om.orElse(null);
        if (m == null) {
            return null;
        }
        try {
            Object o = m.invoke(update);
            return o instanceof MovementStates ms ? ms : null;
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    private static Method findMovementStatesGetter(Class<?> c) {
        for (Class<?> k = c; k != null && k != Object.class; k = k.getSuperclass()) {
            for (Method m : k.getDeclaredMethods()) {
                if (m.getParameterCount() != 0) {
                    continue;
                }
                if (!MovementStates.class.isAssignableFrom(m.getReturnType())) {
                    continue;
                }
                try {
                    m.setAccessible(true);
                } catch (Exception e) {
                    continue;
                }
                return m;
            }
        }
        return null;
    }

    private static byte quantizeWish(PlayerInput.InputUpdate update) {
        try {
            Method m = update.getClass().getMethod("wishMovement");
            Object w = m.invoke(update);
            if (w == null) {
                return 0;
            }
            Method ox = w.getClass().getMethod("getX");
            Method oz = w.getClass().getMethod("getZ");
            float x = ((Number) ox.invoke(w)).floatValue();
            float z = ((Number) oz.invoke(w)).floatValue();
            double ang = Math.atan2(x, z);
            int oct = (int) Math.floor((ang + Math.PI) / (Math.PI / 4)) & 7;
            return (byte) (oct + 1);
        } catch (ReflectiveOperationException e) {
            return 0;
        }
    }

    private static byte quantizeVelY(PlayerInput.InputUpdate update) {
        try {
            Method m = update.getClass().getMethod("velocity");
            Object v = m.invoke(update);
            if (v == null) {
                return 0;
            }
            Method gy = v.getClass().getMethod("getY");
            float y = ((Number) gy.invoke(v)).floatValue();
            int sign = y > 0.05f ? 1 : (y < -0.05f ? 2 : 0);
            int mag = (int) Math.min(15, Math.abs(y) * 4.0f);
            return (byte) ((sign << 4) | mag);
        } catch (ReflectiveOperationException e) {
            return 0;
        }
    }

    private static int hashInteractionTypes(Object update) {
        return update.getClass().hashCode() ^ System.identityHashCode(update);
    }

    private static long extractClientTimestamp(Object update) {
        try {
            Method m = update.getClass().getMethod("clientTimestamp");
            Object v = m.invoke(update);
            return v instanceof Number n ? n.longValue() : 0L;
        } catch (ReflectiveOperationException e) {
            try {
                Method m = update.getClass().getMethod("getClientTimestamp");
                Object v = m.invoke(update);
                return v instanceof Number n ? n.longValue() : 0L;
            } catch (ReflectiveOperationException ex) {
                return 0L;
            }
        }
    }
}
