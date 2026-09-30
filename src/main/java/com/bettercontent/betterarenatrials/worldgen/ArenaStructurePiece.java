package com.bettercontent.betterarenatrials.worldgen;

import com.bettercontent.betterarenatrials.registry.ArenaBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;

public final class ArenaStructurePiece extends StructurePiece {
    private final BlockPos center;

    public ArenaStructurePiece(BlockPos center) {
        super(ArenaStructures.ARENA_PIECE.get(), 0, new BoundingBox(center.getX(), -64,
                center.getZ(), center.getX(), 320, center.getZ()));
        this.center = center.immutable();
    }

    public ArenaStructurePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        this(new BlockPos(tag.getInt("cx"), tag.getInt("cy"), tag.getInt("cz")));
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("cx", center.getX()); tag.putInt("cy", center.getY()); tag.putInt("cz", center.getZ());
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator,
                            RandomSource random, BoundingBox box, ChunkPos chunkPos, BlockPos pivot) {
        BlockPos pos = new BlockPos(center.getX(),
                level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, center.getX(), center.getZ()), center.getZ());
        if (!box.isInside(pos) || !level.getWorldBorder().isWithinBounds(pos)) return;
        BlockPos ground = pos.below();
        if (!level.getBlockState(ground).isFaceSturdy(level, ground, net.minecraft.core.Direction.UP)
                || !level.getFluidState(ground).isEmpty()
                || !level.getBlockState(pos).canBeReplaced()
                || !level.getFluidState(pos).isEmpty()) return;
        level.setBlock(pos, ArenaBlocks.ARENA_TOTEM.get().defaultBlockState(), 2);
    }
}
