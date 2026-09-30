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
        helper.succeed();
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
