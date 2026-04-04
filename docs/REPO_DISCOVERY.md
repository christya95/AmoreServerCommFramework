# Repo discovery (Hytale JossDoubleJump + Pebble host)

Generated during framework design. **No coding assumptions** beyond what is verified below.

## Hytale_JossDoubleJump (`D:\Users\josua\workspace\Hytale_JossDoubleJump`)

| Topic | Finding |
|--------|---------|
| **Language** | Java (JDK 25 per `scripts/build.ps1`) |
| **Build** | **Not Gradle/Maven** — PowerShell `scripts/build.ps1` runs `javac` against `HytaleServer.jar`, template `JossDoubleJump.jar`, and `Hyxin*.jar`; outputs `dist/JossDoubleJump.jar` |
| **Plugin entry** | `ca.joss.jossdoublejump.DoubleJump` extends `JavaPlugin`; `manifest.json` → `"Main": "ca.joss.jossdoublejump.DoubleJump"` |
| **Bootstrap (`setup`)** | Registers `DoubleJumpComponent`, `Double_Jump` interaction, `PlayerJoinDoubleJumpAdder`, `DoubleJumpTicking.QueueScannerSystem`, `DoubleJumpTicking.AfterInputSystem`, commands; optional `GlobalAbilityUnlocker.inject()` |
| **PacketAdapters** | **No `PacketAdapter` / inbound packet watchers** in this repo. Movement data enters via `PlayerInput` and `PlayerInput.queue` (see mixin). Framework capture hooks **`PlayerInput.InputUpdate`** at the same injection site as `PlayerInputQueueMixin` (HEAD of `queue`) unless a future API registers true packet adapters. |
| **Per-tick ECS** | `com.hypixel.hytale.component.system.tick.EntityTickingSystem` — `QueueScannerSystem` **BEFORE** `PlayerSystems.ProcessPlayerInput`; `AfterInputSystem` **AFTER** `PlayerSystems.ProcessPlayerInput` |
| **Double-jump FSM** | `DoubleJumpComponent`: `Phase` (GROUNDED / AIR_CAN_DOUBLE / AIR_SPENT) + `InputState` (WAITING_FOR_PRESS / HELD / COOLDOWN_FRAMES). Queue fields: `pendingQueueJumpEdge`, `queueJumpEdgeBufferUntilMs`, `jumpHeldLastQueue`, `movementQueueHadSms`, counters |
| **Decision / apply** | Signal arbitration in `AfterInputSystem.tick` (raw → divergent MS → queueSmsLive → queueSms → fallback). `requestSecondJump` from edge/tapAssist/synthetic + `queueEdge`. **`DoubleJumpTicking.tryApply`** applies impulse / stamina / charges. **Ability path**: `DoubleJumpInteraction.firstRun` → `tryApply` |

## PebbleHostServerRoot

| Topic | Finding |
|--------|---------|
| **Role** | Intended as dedicated server deployment root (mods, configs). **No Java sources or Gradle build** were present in the workspace snapshot used for discovery (empty or not checked out here). Joss build resolves `HytaleServer.jar` via `PEBBLE_SERVER_ROOT` or sibling `PebbleHotServerRoot` per `scripts/build.ps1`. |

## Implications for AmoreServerCommFramework

- **Multi-module Gradle** would not match the existing Joss pipeline; ship **one `AmoreServerCommCore.jar`** (or merged classes) and add it to `javac` **classpath** + unpack into the mod JAR output.
- **Mailbox writers** should mirror the **movement queue ingress** (mixin on `PlayerInput.queue`) until explicit `PacketAdapter` APIs are wired.
- **Trace tick alignment** for FSM/queue/apply: hooks belong in **`AfterInputSystem`** (and optionally `QueueScannerSystem` for queue-only fields), not only in `tryApply`.

Alternative product names (documentation): **AmoreInputFlightRecorder**, **AmoreAbilityTraceKit**, **AmoreMovementTelemetry**.
