package com.bettercontent.betterarenatrials.diagnostics;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class FeatureTelemetryTest {
    @Test public void nativeOutcomeIsNotMisreportedOrExpandedIntoPrivatePayload() {
        List<String> records = new ArrayList<>();
        PlaytestLog log = new PlaytestLog("better_arena_trials", new PlaytestLog.Sink() {
            public boolean enabled(String category, String level) { return true; }
            public void write(String category, String level, String message) { records.add(message); }
        }, () -> 0L, new Properties());
        FeatureTelemetry.trial(log, new PlaytestLog.Context("server", "minecraft:overworld", 42,
                "7ac1c622-f1a4-4986-9404-70bf2345061c", "10,64,-3"), false);
        if (records.size() != 1 || !records.get(0).contains("result=rolled_back reason=wave_insertion_failed")
                || !records.get(0).contains("subject=7ac1c622-f1a4-4986-9404-70bf2345061c")
                || !records.get(0).contains("dimension=minecraft:overworld") || !records.get(0).contains("tick=42")
                || !records.get(0).contains("pos=10,64,-3") || records.get(0).contains("archive=")
                || records.get(0).contains("player=") || records.get(0).contains("payload=")) {
            throw new AssertionError(records.toString());
        }
        var other = java.util.UUID.fromString("341f78c8-47eb-4c02-a7ea-46a0c9081785");
        FeatureTelemetry.duel(log, new PlaytestLog.Context("server", "minecraft:overworld", 42,
                "7ac1c622-f1a4-4986-9404-70bf2345061c", "10,64,-3"), other, 42);
        FeatureTelemetry.equipment(log, new PlaytestLog.Context("server", "minecraft:overworld", 44, other.toString(), "10,64,-3"), 42);
        if (records.size() != 3 || !records.get(1).contains("other_subject=" + other)
                || !records.get(2).contains("subject=" + other) || !records.get(2).contains("pos=10,64,-3")
                || !records.get(2).contains("dimension=minecraft:overworld") || !records.get(2).contains("tick=44")
                || !records.get(2).contains("started_tick=42") || !records.get(2).contains("result=restored reason=snapshot_consumed")
                || records.get(2).contains("player=") || records.get(2).contains("payload=") || records.get(2).contains("seed=")) {
            throw new AssertionError(records.toString());
        }
    }
    public static void main(String[] args) { new FeatureTelemetryTest().nativeOutcomeIsNotMisreportedOrExpandedIntoPrivatePayload(); }
}
