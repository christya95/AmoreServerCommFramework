package amore.servercomm.api;

import amore.servercomm.registry.PerPlayerTraceState;
import amore.servercomm.registry.ServerCommRegistry;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;

/** Facade for integrators (abilities, mods). */
public final class ServerComm {

    private ServerComm() {}

    public static PerPlayerTraceState stateFor(PlayerInput input) {
        return ServerCommRegistry.stateFor(input);
    }

    public static boolean isTracing(String username) {
        return ServerCommRegistry.isTracing(username);
    }

    public static void setTracing(String username, boolean on) {
        ServerCommRegistry.setTracing(username, on);
    }
}
