package amore.servercomm.api;

/** Outcome of an ability apply attempt (e.g. double jump) for tracing. */
public enum ApplyResult {
    NONE,
    APPLIED,
    REJECT_NOT_AIRBORNE,
    REJECT_NO_CHARGES,
    REJECT_COOLDOWN,
    REJECT_STAMINA,
    REJECT_NO_MOVEMENT_COMPONENT,
    REJECT_NOT_REQUESTED
}
