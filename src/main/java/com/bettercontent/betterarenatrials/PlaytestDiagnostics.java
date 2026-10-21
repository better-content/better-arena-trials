package com.bettercontent.betterarenatrials;

import com.bettercontent.betterarenatrials.diagnostics.PlaytestLog;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/** Native lifecycle adapter; retains no level, player or entity references. */
@Mod.EventBusSubscriber(modid = "better_arena_trials")
public final class PlaytestDiagnostics {
    public static final PlaytestLog LOG = new PlaytestLog("better_arena_trials");
    private PlaytestDiagnostics() {}

    @SubscribeEvent public static void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) LOG.summary("server");
    }
    @SubscribeEvent public static void stopped(ServerStoppedEvent event) { LOG.reset("server"); }

    @Mod.EventBusSubscriber(modid = "better_arena_trials", value = net.minecraftforge.api.distmarker.Dist.CLIENT)
    public static final class ClientLifecycle {
        @SubscribeEvent public static void disconnect(net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggingOut event) {
            LOG.reset("client");
        }
    }

    @Mod.EventBusSubscriber(modid = "better_arena_trials", bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class Startup {
        @SubscribeEvent public static void setup(FMLCommonSetupEvent event) {
            String version = net.minecraftforge.fml.ModList.get().getModContainerById("better_arena_trials")
                    .map(container -> container.getModInfo().getVersion().toString()).orElse("UNKNOWN");
            var modFile = net.minecraftforge.fml.ModList.get().getModFileById("better_arena_trials");
            String source = modFile == null ? "UNKNOWN" : LOG.sourceIdentity(modFile.getFile().findResource("META-INF", "better-content-source.properties"));
            LOG.status("common", version, source, () -> PlaytestLog.fields(
                    "enabled", true, "enabled_source", "mod_loaded", "integration_player_traces", net.minecraftforge.fml.ModList.get().isLoaded("better_player_traces")));
        }
    }
}
