package com.bettercontent.betterarenatrials.diagnostics;

/** Pure observation of the owning feature's existing outcome, with no native reads. */
public final class FeatureTelemetry {
    private FeatureTelemetry() {}
    public static void duel(PlaytestLog log, PlaytestLog.Context context, java.util.UUID other, long startedTick) {
        log.debug("match", "arena.duel", context, "started", "invitation_accepted",
                () -> PlaytestLog.fields("other_subject", other, "started_tick", startedTick));
    }
    public static void equipment(PlaytestLog log, PlaytestLog.Context context, long startedTick) {
        log.debug("match", "arena.equipment", context, "restored", "snapshot_consumed",
                () -> PlaytestLog.fields("started_tick", startedTick >= 0 ? Long.toString(startedTick) : "UNKNOWN"));
    }
    public static void trial(PlaytestLog log, PlaytestLog.Context context, boolean inserted) {
        log.debug("match", "arena.trial", context, inserted ? "started" : "rolled_back", inserted ? "wave_inserted" : "wave_insertion_failed", null);
    }
}
