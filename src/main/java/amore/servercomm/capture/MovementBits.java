package amore.servercomm.capture;

import com.hypixel.hytale.protocol.MovementStates;

/** Pack movement enums/bools into a stable int for traces (bit layout is versioned informally). */
public final class MovementBits {

    private MovementBits() {}

    public static int pack(MovementStates ms) {
        if (ms == null) {
            return 0;
        }
        int b = 0;
        if (ms.jumping) {
            b |= 1;
        }
        if (ms.onGround) {
            b |= 2;
        }
        if (ms.inFluid) {
            b |= 4;
        }
        if (ms.climbing) {
            b |= 8;
        }
        return b;
    }
}
