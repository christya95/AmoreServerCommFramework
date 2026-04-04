package amore.servercomm.registry;

import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Weak-keyed {@link PlayerInput} → {@link PerPlayerTraceState}; username-keyed tracing enable flags.
 */
public final class ServerCommRegistry {

    private static final Map<PlayerInput, PerPlayerTraceState> BY_INPUT =
        Collections.synchronizedMap(new java.util.WeakHashMap<>());
    private static final Set<String> TRACING_USERS = ConcurrentHashMap.newKeySet();
    private static final AtomicLong GLOBAL_TICK = new AtomicLong();

    private ServerCommRegistry() {}

    public static long nextGlobalTick() {
        return GLOBAL_TICK.incrementAndGet();
    }

    public static PerPlayerTraceState stateFor(PlayerInput input) {
        if (input == null) {
            return null;
        }
        return BY_INPUT.computeIfAbsent(input, k -> new PerPlayerTraceState());
    }

    public static boolean isTracing(String username) {
        return username != null && TRACING_USERS.contains(username);
    }

    public static void setTracing(String username, boolean on) {
        if (username == null) {
            return;
        }
        if (on) {
            TRACING_USERS.add(username);
        } else {
            TRACING_USERS.remove(username);
        }
    }
}
