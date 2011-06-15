package com.iminurnetz.bukkit.plugin.longgrass.tests;

import java.util.Random;

import junit.framework.TestCase;

import com.iminurnetz.bukkit.plugin.longgrass.LGChunk;

public class ChunkCoordinatesTest extends TestCase {
    public void testCoordinates() {
        
        Random random = new Random();
        
        LGChunk chunk = new LGChunk(random.nextInt(128), random.nextInt(128), "test");
        
        int bx = chunk.getX() << 4;
        int bz = chunk.getZ() << 4;
        
        for (int n = 0; n < 128; ++n) {
            int tx = bx + random.nextInt(16) + 8;
            int ty = random.nextInt(128);
            int tz = bz + random.nextInt(16) + 8;
            checkWithin(chunk, random, tx, ty, tz);
        }

    }

    private void checkWithin(LGChunk chunk, Random random, int x, int y, int z) {
        System.out.println("Chunk is " + (chunk.getX() << 4) + "x" + (chunk.getZ() << 4));
        for (int n = 0; n < 128; ++n) {
            int tx = x + random.nextInt(8) - random.nextInt(8);
            int ty = y + random.nextInt(4) - random.nextInt(4);
            int tz = z + random.nextInt(8) - random.nextInt(8);
            
            boolean isWithin = (int) (tx >> 4) == chunk.getX() &&
                (int) (tz >> 4) == chunk.getZ();
            
            System.out.println(tx + "x" + tz + " is " + (isWithin ? "" : "NOT ") + "in chunk " + chunk.getX() + "x" + chunk.getZ());
        }
    }
}
