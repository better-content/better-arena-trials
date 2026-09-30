package com.bettercontent.betterarenatrials.server;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.ArrayList;
import java.util.List;

/** The natural terrain around a totem is the fight area; no blocks mark its edge. */
public final class ArenaArea {
    public static final int RANGE = 48;
    public static final int EXIT_GRACE_TICKS = 20 * 10;

    private ArenaArea() {}

    public static boolean contains(double x, double y, double z, BlockPos totem) {
        double dx = x - (totem.getX() + 0.5);
        double dy = y - (totem.getY() + 0.5);
        double dz = z - (totem.getZ() + 0.5);
        return dx * dx + dy * dy + dz * dz <= (double) RANGE * RANGE;
    }

    public static int secondsLeft(long leftAt, long now) {
        return Math.max(0, (int) ((EXIT_GRACE_TICKS - (now - leftAt) + 19) / 20));
    }

    public static List<BlockPos> trialSpawns(ServerLevel level, BlockPos totem, int count) {
        List<BlockPos> positions = new ArrayList<>();
        for (int radius = 8; radius <= 20 && positions.size() < count; radius += 2) {
            for (int step = 0; step < 24 && positions.size() < count; step++) {
                double angle = Math.PI * 2 * step / 24 + radius;
                int x = totem.getX() + (int) Math.round(Math.cos(angle) * radius);
                int z = totem.getZ() + (int) Math.round(Math.sin(angle) * radius);
                BlockPos feet = surface(level, x, z);
                if (feet == null || !contains(feet.getX() + .5, feet.getY(), feet.getZ() + .5, totem)) continue;
                if (positions.stream().anyMatch(other -> other.distSqr(feet) < 16)) continue;
                positions.add(feet);
            }
        }
        return positions;
    }

    public static BlockPos safeReturn(ServerLevel level, BlockPos totem) {
        for (int radius = 1; radius <= 5; radius++) {
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    if (Math.max(Math.abs(dx), Math.abs(dz)) != radius) continue;
                    BlockPos feet = surface(level, totem.getX() + dx, totem.getZ() + dz);
                    if (feet != null && contains(feet.getX() + .5, feet.getY(), feet.getZ() + .5, totem)) return feet;
                }
            }
        }
        return null;
    }

    private static BlockPos surface(ServerLevel level, int x, int z) {
        if (!level.hasChunkAt(new BlockPos(x, level.getSeaLevel(), z))) return null;
        BlockPos feet = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z), z);
        if (!level.getWorldBorder().isWithinBounds(feet)) return null;
        BlockPos ground = feet.below();
        if (!level.getBlockState(ground).isFaceSturdy(level, ground, Direction.UP)
                || !level.getFluidState(ground).isEmpty()) return null;
        if (!level.getBlockState(feet).getCollisionShape(level, feet).isEmpty()
                || !level.getBlockState(feet.above()).getCollisionShape(level, feet.above()).isEmpty()
                || !level.getFluidState(feet).isEmpty()
                || !level.getFluidState(feet.above()).isEmpty()) return null;
        return feet;
    }
}
