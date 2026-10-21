package com.bettercontent.betterarenatrials.server;

import com.bettercontent.betterarenatrials.PlaytestDiagnostics;
import com.bettercontent.betterarenatrials.diagnostics.PlaytestLog;
import com.bettercontent.betterplayertraces.TracesMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.ModList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/** Typed required-server-provider boundary; Player Traces owns persistent arena replays. */
public final class ArenaTraceBridge {
    private static final Logger FAILURES = LoggerFactory.getLogger("bc.playtest.better_arena_trials.archive");
    private ArenaTraceBridge() {}

    public static boolean available() { return ModList.get().isLoaded("better_player_traces"); }

    public static int count(ServerLevel level, String arenaId) {
        try {
            return TracesMod.arenaDuelCount(level, arenaId);
        } catch (Exception error) {
            // Retain the existing empty-gallery fallback, but never silently report an IO failure as emptiness.
            FAILURES.error("Player Traces arena archive count operation failed", error);
            return 0;
        }
    }

    public static int appendIndex(ServerLevel level, String arenaId) { return count(level, arenaId); }

    public static void append(ServerLevel level, String arenaId, CompoundTag record) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            NbtIo.writeCompressed(record, bytes);
            byte[] encoded = bytes.toByteArray();
            int index = TracesMod.appendArenaDuel(level, arenaId, encoded);
            if (PlaytestDiagnostics.LOG.enabled("archive")) PlaytestDiagnostics.LOG.debug("archive", "arena.archive_append",
                    PlaytestLog.Context.server(level.dimension().location().toString(), level.getGameTime()),
                    "published", "provider_returned", () -> PlaytestLog.fields("index", index, "bytes", encoded.length));
        } catch (Exception error) {
            // Preserve the existing thrown failure; the caller/native handler owns its failure reporting.
            throw new IllegalStateException("Player Traces could not archive the arena duel", error);
        }
    }

    public static CompoundTag get(ServerLevel level, String arenaId, int index) {
        try {
            byte[] bytes = TracesMod.arenaDuel(level, arenaId, index);
            CompoundTag record = bytes == null ? null : NbtIo.readCompressed(new ByteArrayInputStream(bytes));
            if (PlaytestDiagnostics.LOG.enabled("archive")) PlaytestDiagnostics.LOG.debug("archive", "arena.archive_read",
                    PlaytestLog.Context.server(level.dimension().location().toString(), level.getGameTime()),
                    record == null ? "missing" : "decoded", record == null ? "record_not_found" : "provider_record",
                    () -> PlaytestLog.fields("index", index));
            return record;
        } catch (Exception error) {
            FAILURES.error("Player Traces arena archive read operation failed", error);
            return null;
        }
    }
}
