package com.bettercontent.betterarenatrials;

import com.bettercontent.betterarenatrials.registry.ArenaBlocks;
import com.bettercontent.betterarenatrials.server.ArenaArea;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(ArenaChallenges.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ArenaChallengesGameTests {
    private ArenaChallengesGameTests() {}

    public static void register(RegisterGameTestsEvent event) {
        event.register(ArenaChallengesGameTests.class);
    }

    @GameTest(templateNamespace = "minecraft", template = "empty")
    public static void arenaTotemIsRegistered(GameTestHelper helper) {
        ResourceLocation id = new ResourceLocation(ArenaChallenges.MOD_ID, "arena_totem");
        helper.assertTrue(ArenaBlocks.ARENA_TOTEM.isPresent(), "Arena Totem block registration is missing");
        helper.assertTrue(BuiltInRegistries.ITEM.containsKey(id), "Arena Totem item registration is missing");
        for (String name : java.util.List.of("champion_blade", "warden_axe", "duelist_sigil"))
            helper.assertTrue(BuiltInRegistries.ITEM.containsKey(new ResourceLocation(ArenaChallenges.MOD_ID,name)), "Existing Arena kit/reward item missing: " + name);
        helper.succeed();
    }

    @GameTest(templateNamespace = "minecraft", template = "empty", timeoutTicks = 100)
    public static void lifeRecordsReadNativeMatchDeathAndRespawnContainment(GameTestHelper helper) {
        var level = helper.getLevel();
        BlockPos origin = helper.absolutePos(new BlockPos(0,2,0));
        BlockPos totem = new BlockPos(origin.getX(),140,origin.getZ());
        for (int dx = -10; dx <= 10; dx++) for (int dz = -10; dz <= 10; dz++) {
            BlockPos feet = totem.offset(dx,0,dz);
            level.setBlockAndUpdate(feet.below(), Blocks.GRASS_BLOCK.defaultBlockState());
            level.setBlockAndUpdate(feet, Blocks.AIR.defaultBlockState());
            level.setBlockAndUpdate(feet.above(), Blocks.AIR.defaultBlockState());
        }
        level.setBlockAndUpdate(totem, ArenaBlocks.ARENA_TOTEM.get().defaultBlockState());
        var site = (com.bettercontent.betterarenatrials.block.ArenaTotemBlockEntity) level.getBlockEntity(totem);
        var uuid = java.util.UUID.randomUUID();
        var player = new net.minecraft.server.level.ServerPlayer(level.getServer(), level,
            new com.mojang.authlib.GameProfile(uuid,"arena-"+uuid.toString().substring(0,8)));
        var wire = new net.minecraft.network.Connection(net.minecraft.network.protocol.PacketFlow.SERVERBOUND);
        var channel = new io.netty.channel.embedded.EmbeddedChannel(wire);
        level.getServer().getPlayerList().placeNewPlayer(wire,player);
        player.setGameMode(net.minecraft.world.level.GameType.SURVIVAL); player.setNoGravity(true);
        player.setPos(totem.getX()+.5, totem.getY(), totem.getZ()+2.5);
        Runnable cleanup = () -> { level.getServer().getPlayerList().remove(player); player.discard(); channel.finishAndReleaseAll(); };
        helper.runAtTickTime(65, () -> {
            try {
                helper.assertTrue(!com.bettercontent.betterarenatrials.server.ArenaMatchService.containsLifeRecordParticipant(player), "Nonparticipant must not be contained");
                com.bettercontent.betterarenatrials.server.ArenaMatchService.handleButton(player,site,1);
                helper.assertTrue(com.bettercontent.betterarenatrials.server.ArenaMatchService.containsLifeRecordParticipant(player), "Native trial start must suspend life collection");
                player.hurt(player.damageSources().fellOutOfWorld(),1000);
                helper.assertTrue(!player.isAlive(), "Native Arena death fixture must actually die");
                helper.assertTrue(com.bettercontent.betterarenatrials.server.ArenaMatchService.containsLifeRecordParticipant(player), "Defeated native participant must remain contained pending respawn");
                // Exercise the native owner's respawn listener without manufacturing persistent tags.
                com.bettercontent.betterarenatrials.server.ArenaMatchService.onRespawn(new net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent(player,false));
                helper.assertTrue(!com.bettercontent.betterarenatrials.server.ArenaMatchService.containsLifeRecordParticipant(player), "Respawn must release death containment");
                helper.assertTrue(com.bettercontent.betterarenatrials.server.ArenaMatchService.suspendsLifeRecordCollection(player), "The finishing tick must remain collection-suspended");
                helper.runAtTickTime(helper.getTick()+1, () -> {
                    try {
                        helper.assertTrue(!com.bettercontent.betterarenatrials.server.ArenaMatchService.suspendsLifeRecordCollection(player), "Finish collection guard must be bounded to its native tick");
                        cleanup.run(); helper.succeed();
                    } catch (Throwable failure) { cleanup.run(); helper.fail(failure.toString()); }
                });
            } catch (Throwable failure) { cleanup.run(); helper.fail(failure.toString()); }
        });
    }

    @GameTest(templateNamespace = "minecraft", template = "empty")
    public static void trialSpawnsUseUnchangedNaturalGround(GameTestHelper helper) {
        var level = helper.getLevel();
        BlockPos origin = helper.absolutePos(new BlockPos(0, 2, 0));
        BlockPos totem = new BlockPos(origin.getX(), 100, origin.getZ());
        for (int dx = -10; dx <= 10; dx++) {
            for (int dz = -10; dz <= 10; dz++) {
                BlockPos feet = totem.offset(dx, 0, dz);
                level.setBlock(feet.below(), Blocks.GRASS_BLOCK.defaultBlockState(), 3);
                level.setBlock(feet, Blocks.AIR.defaultBlockState(), 3);
                level.setBlock(feet.above(), Blocks.AIR.defaultBlockState(), 3);
            }
        }
        level.setBlock(totem, ArenaBlocks.ARENA_TOTEM.get().defaultBlockState(), 3);
        var positions = ArenaArea.trialSpawns(level, totem, 5);
        helper.assertTrue(positions.size() == 5, "Trial needs five safe ground positions; found " + positions.size());
        for (BlockPos feet : positions) {
            helper.assertTrue(level.getBlockState(feet.below()).is(Blocks.GRASS_BLOCK), "Spawn ground was changed");
            helper.assertTrue(level.getBlockState(feet).isAir(), "Spawn feet must be clear");
            helper.assertTrue(level.getBlockState(feet.above()).isAir(), "Spawn headroom must be clear");
            helper.assertTrue(ArenaArea.contains(feet.getX() + .5, feet.getY(), feet.getZ() + .5, totem),
                    "Spawn must remain near the totem");
        }
        helper.succeed();
    }
}
