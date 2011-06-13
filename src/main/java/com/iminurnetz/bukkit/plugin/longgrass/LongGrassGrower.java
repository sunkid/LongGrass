/**
 * LICENSING
 * 
 * This software is copyright by sunkid <sunkid@iminurnetz.com> and is
 * distributed under a dual license:
 * 
 * Non-Commercial Use:
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Commercial Use:
 *    Please contact sunkid@iminurnetz.com
 */
package com.iminurnetz.bukkit.plugin.longgrass;

import java.util.Date;
import java.util.Hashtable;
import java.util.Random;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import com.iminurnetz.bukkit.util.MaterialUtils;
import com.iminurnetz.bukkit.util.SerializableBlockType;

public class LongGrassGrower {

    private final WorldSortedLGChunkList chunks;
    private final LongGrassPlugin plugin;
    
    private static LongGrassGrower instance = null;
    
    private LongGrassGrower(LongGrassPlugin plugin) {
        this.plugin = plugin;
        this.chunks = plugin.getChunks();
    }
    
    public void populateChunk(Chunk chunk) {
        populateChunk(chunk, true);
    }
    
    public void populateChunk(Chunk chunk, boolean checkFirst) {
        
        synchronized (chunk) {
            LGChunk lgChunk = new LGChunk(chunk);

            if (chunks.contains(lgChunk)) {
                return;
            }

            // see if long grass or dead bushes exists in this chunk
            if (!checkFirst || !doesChunkHavePlants(chunk)) {
                plugin.debug("Growing plants in chunk " + chunk.getX() + "x" + chunk.getZ());
                growPlants(chunk);
            }

            chunks.add(lgChunk);
        }
    }
    
    public void regrowPlants() {        
        Date now = new Date();
        long growthPeriod = 1000 * (long) plugin.getConfig().getGrowTime();
        long period = growthPeriod;
        
        World lastWorldProcessed = null;
        int rainFactor = plugin.getConfig().getRainFactor();
        Server server = plugin.getServer();

        for (LGChunk lgChunk : chunks) {
            synchronized (lgChunk) {

                World world = server.getWorld(lgChunk.getWorld());
                if (lastWorldProcessed == null || lastWorldProcessed != world) {
                    lastWorldProcessed = world;
                    period = world.hasStorm() ? growthPeriod / rainFactor : growthPeriod;
                }
                
                if (lgChunk.getLastMowed() == null || now.before(new Date(lgChunk.getLastMowed().getTime() + period))) {
                    continue;
                }
                
                lgChunk.setLastMowed(null);
                
                boolean wasLoaded = false;

                if (!world.isChunkLoaded(lgChunk.getX(), lgChunk.getZ()) &&
                        world.loadChunk(lgChunk.getX(), lgChunk.getZ(), false)) {
                    wasLoaded = true;
                }

                Chunk chunk = world.getChunkAt(lgChunk.getX(), lgChunk.getZ());
                plugin.debug("Processing " + chunk + " (" + wasLoaded + ")");
                
                Hashtable<LGCoordinate, Date> lastMowed = lgChunk.getBlockLastMowed();
                Hashtable<LGCoordinate, SerializableBlockType> plants = lgChunk.getPlantMaterial();

                for (LGCoordinate coordinate : lgChunk.getBlockLastMowed().keySet()) {
                    Date growDate = new Date(lastMowed.get(coordinate).getTime() + period);
                    if (now.after(growDate)) {
                        Block block = chunk.getBlock(coordinate.getX(), coordinate.getY(), coordinate.getZ());
                        SerializableBlockType type = plants.get(coordinate);
                        Material m = Material.getMaterial(type.getMaterial());
                        block.setTypeIdAndData(m.getId(), type.getData(), false);
                    }
                }

                if (wasLoaded) {
                    world.unloadChunk(lgChunk.getX(), lgChunk.getZ());
                }
            }
        }
    }

    private boolean doesChunkHavePlants(Chunk chunk) {
        int bx = chunk.getX() << 4;
        int bz = chunk.getZ() << 4;

        World world = chunk.getWorld();

        for (int xx = bx; xx < bx + 16; xx++) {
            for (int zz = bz; zz < bz + 16; zz++) {
                for (int yy = 0; yy < 128; yy++) {
                    int typeId = world.getBlockTypeIdAt(xx, yy, zz);                    
                    if (typeId == Material.LONG_GRASS.getId() || typeId == Material.DEAD_BUSH.getId()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
    
    // very much inspired by code in net.minecraft.server.ChunkProviderGenerate
    private void growPlants(Chunk chunk) {
        
        if (!chunk.getWorld().isChunkLoaded(chunk)) {
            return;
        }
        
        int bx = chunk.getX() << 4;
        int bz = chunk.getZ() << 4;

        int tx;
        int tz;
        int ty;

        Block block = chunk.getBlock(bx, 64, bz);
        Biome biome = block.getBiome();
        
        Random random = new Random();
        random.setSeed(chunk.getWorld().getSeed());
        long i = random.nextLong() / 2L * 2L + 1L;
        long j = random.nextLong() / 2L * 2L + 1L;

        random.setSeed((long) chunk.getX() * i + (long) chunk.getZ() * j ^ chunk.getWorld().getSeed());
        
        int maxDensity = 0;
        switch (biome) {
        case FOREST:
            maxDensity = 2;
            break;

        case RAINFOREST:
            maxDensity = 10;
            break;

        case SEASONAL_FOREST:
            maxDensity = 2;
            break;

        case TAIGA:
            maxDensity = 1;
            break;

        case PLAINS:
            maxDensity = 10;
            break;
        }

        for (int n = 0; n < maxDensity; ++n) {
            byte data = 1;

            if (biome == Biome.RAINFOREST && random.nextInt(3) != 0) {
                data = 2;
            }

            tx = bx + random.nextInt(16) + 8;
            ty = random.nextInt(128);
            tz = bz + random.nextInt(16) + 8;
            
            plugin.debug("randomizing growth around " + tx + ", " + ty + ", " + tz + " based on " + bx + "x" + bz);
            
            randomizeGrowth(chunk, random, Material.LONG_GRASS, data, tx, ty, tz);
        }
        
        maxDensity = biome == Biome.DESERT ? 2 : 0;
        
        for (int n = 0; n < maxDensity; ++n) {
            tx = bx + random.nextInt(16) + 8;
            ty = random.nextInt(128);
            tz = bz + random.nextInt(16) + 8;
            randomizeGrowth(chunk, random, Material.DEAD_BUSH, (byte) 0, tx, ty, tz);
        }
    }

    // almost identical to net.minecraft.server.WorldGenFlowers
    private boolean randomizeGrowth(Chunk chunk, Random random, Material material, byte data, int x, int y, int z) {
        World world = chunk.getWorld();
        
        int targetBlockMaterialId = world.getBlockTypeIdAt(x, y, z);

        while ((targetBlockMaterialId == 0 || targetBlockMaterialId == Material.LEAVES.getId()) && y > 0) {
            targetBlockMaterialId = world.getBlockTypeIdAt(x, --y, z);
        }

        int max = material == Material.LONG_GRASS ? 128 : 4;
        for (int n = 0; n < max; ++n) {
            int tx = x + random.nextInt(8) - random.nextInt(8);
            int ty = y + random.nextInt(4) - random.nextInt(4);
            int tz = z + random.nextInt(8) - random.nextInt(8);
            Block block = world.getBlockAt(tx, ty, tz);
            if (block.getTypeId() == 0 && canGrowHere(block, material)) {
                plugin.debug("Growing " + material + " at " + block.getLocation());
                block.setTypeIdAndData(material.getId(), data, false);
                
                if (!isInChunk(chunk, block)) {
                    populateChunk(block.getChunk(), false);
                }
            }
        }

        return true;
    }

    private boolean isInChunk(Chunk chunk, Block block) {
        Chunk bChunk = block.getChunk();
        return chunk.getX() == bChunk.getX() && chunk.getZ() == bChunk.getZ();
    }

    // same logic as in net.minecraft.server.BlockFlower/BlockDeadBush
    private boolean canGrowHere(Block block, Material material) {
        Material targetMaterial = block.getRelative(BlockFace.DOWN).getType();
        return (block.getLightLevel() >= 8 && 
                
                (
                  (material == Material.LONG_GRASS && MaterialUtils.isSameMaterial(targetMaterial, Material.DIRT, Material.GRASS, Material.SOIL)) || 
                  (material == Material.DEAD_BUSH && targetMaterial == Material.SAND))
                );
    }

    public static LongGrassGrower getInstance(LongGrassPlugin plugin) {
        if (instance == null) {
            instance = new LongGrassGrower(plugin);
        }
        
        return instance;
    }
}
