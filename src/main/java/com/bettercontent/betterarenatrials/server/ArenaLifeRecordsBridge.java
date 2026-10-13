package com.bettercontent.betterarenatrials.server;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/** Optional typed read-only integration; Arena remains sole authority for match containment. */
@Mod.EventBusSubscriber(modid = "better_arena_trials", bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ArenaLifeRecordsBridge {
    private ArenaLifeRecordsBridge() {}
    @SubscribeEvent public static void commonSetup(FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded("better_deaths_door")) event.enqueueWork(() ->
            com.bettercontent.betterdeathsdoor.records.LifeRecords.registerArenaEligibility(ArenaMatchService::containsLifeRecordParticipant, ArenaMatchService::suspendsLifeRecordCollection));
    }
}
