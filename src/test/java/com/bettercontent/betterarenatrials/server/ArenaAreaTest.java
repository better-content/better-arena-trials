package com.bettercontent.betterarenatrials.server;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArenaAreaTest {
    private static final BlockPos TOTEM = new BlockPos(100, 64, -30);

    @Test
    void naturalTerrainRangeIsThreeDimensional() {
        assertTrue(ArenaArea.contains(100.5, 65, -29.5, TOTEM));
        assertTrue(ArenaArea.contains(148.5, 64.5, -29.5, TOTEM));
        assertFalse(ArenaArea.contains(148.6, 64.5, -29.5, TOTEM));
        assertTrue(ArenaArea.contains(120.5, 94.5, -29.5, TOTEM));
        assertFalse(ArenaArea.contains(100.5, 113, -29.5, TOTEM));
    }

    @Test
    void exitGraceCountsDownForTenSecondsAndResetsFromNewExitTime() {
        long leftAt = 1_000;
        assertEquals(10, ArenaArea.secondsLeft(leftAt, leftAt));
        assertEquals(10, ArenaArea.secondsLeft(leftAt, leftAt + 19));
        assertEquals(9, ArenaArea.secondsLeft(leftAt, leftAt + 20));
        assertEquals(1, ArenaArea.secondsLeft(leftAt, leftAt + 199));
        assertEquals(0, ArenaArea.secondsLeft(leftAt, leftAt + 200));
        assertEquals(10, ArenaArea.secondsLeft(leftAt + 120, leftAt + 120));
    }
}
