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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;

import com.iminurnetz.bukkit.plugin.BukkitPlugin;
import com.iminurnetz.bukkit.util.MaterialUtils;

public class LongGrassPlugin extends BukkitPlugin {

    ArrayList<LGChunk> chunks;
    
    @Override
    public void enablePlugin() throws Exception {
        loadChunks();
        LGWorldListener worldListener = new LGWorldListener(this, chunks);
        LGPlayerListener playerListener = new LGPlayerListener(this);
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Lowest, this);
        // this will bomb a big map :(
        // pm.registerEvent(Event.Type.CHUNK_LOAD, worldListener, Priority.Lowest, this);
        // not yet in Bukkit/CraftBukkit
        // pm.registerEvent(Event.Type.CHUNK_CREATE, worldListener, Priority.Lowest, this);
        
        log("enabled");
    }
    
    @Override
    public void onDisable() {
        saveChunkFile();
        log("disabled");
    }

    @SuppressWarnings("unchecked")
    private void loadChunks() {
        
        File chunkCache = getChunksFile();
        if (!chunkCache.exists()) {
            chunks = new ArrayList<LGChunk>();
            return;
        }
        
        try {
            FileInputStream fis = new FileInputStream(chunkCache);
            ObjectInputStream in = new ObjectInputStream(fis);
            chunks = (ArrayList<LGChunk>) in.readObject();
            in.close();
            fis.close();
        } catch (Exception e) {
            log(Level.SEVERE, "Cannot load cached chunks, starting from scratch", e);
        }
    }
    
    private File getChunksFile() {
        return new File(this.getDataFolder(), "chunks.ser");
    }

    private void saveChunkFile() {
        File chunkCache = getChunksFile();
        
        File dataDir = chunkCache.getParentFile();
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        try {
            FileOutputStream fos = new FileOutputStream(chunkCache);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(chunks);
            out.close();
            fos.close();
        } catch (Exception e) {
            log(Level.SEVERE, "Cannot cache chunks", e);
        }
    }
    
    protected synchronized void processChunk(Chunk chunk) {
        LGChunk lgChunk = new LGChunk(chunk);
        
        if (chunks.contains(lgChunk)) {
            return;
        }
        
        // see if long grass or dead bushes exists in this chunk
        if (!doesChunkHavePlants(chunk)) {
            // log("Growing plants in chunk " + chunk.getX() + "x" + chunk.getZ());
            growPlants(chunk);
        }
        
        chunks.add(lgChunk);        
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
    
    private void growPlants(Chunk chunk) {
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
            // log("randomizing growth around " + tx + ", " + ty + ", " + tz + " based on " + bx + "x" + bz);
            randomizeGrowth(chunk.getWorld(), random, Material.LONG_GRASS, data, tx, ty, tz);
        }
        
        maxDensity = biome == Biome.DESERT ? 2 : 0;
        
        for (int n = 0; n < maxDensity; ++n) {
            tx = bx + random.nextInt(16) + 8;
            ty = random.nextInt(128);
            tz = bz + random.nextInt(16) + 8;
            randomizeGrowth(chunk.getWorld(), random, Material.DEAD_BUSH, (byte) 0, tx, ty, tz);
        }
    }

    private boolean randomizeGrowth(World world, Random random, Material material, byte data, int x, int y, int z) {
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
            if (block.getTypeId() == 0 && canGrowFlowersAndGrass(block)) {
                // log("Growing " + material + " at " + block.getLocation());
                block.setTypeIdAndData(material.getId(), data, false);
            }
        }

        return true;
    }

    private boolean canGrowFlowersAndGrass(Block block) {
        return (block.getLightLevel() >= 8 &&
                MaterialUtils.isSameMaterial(block.getRelative(BlockFace.DOWN).getType(), Material.DIRT, Material.GRASS, Material.SOIL));
    }
}
