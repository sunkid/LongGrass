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
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;

import com.iminurnetz.bukkit.plugin.BukkitPlugin;

public class LongGrassPlugin extends BukkitPlugin {

    private WorldSortedLGChunkList chunks;
    private LongGrassGrower grower;
    private LGConfigurationService config;
    private LongGrassMonitor monitor;
    
    @Override
    public void enablePlugin() throws Exception {        
        config = new LGConfigurationService(this);

        loadChunks();
   
        setGrower(LongGrassGrower.getInstance(this));
        
        // LGWorldListener worldListener = new LGWorldListener(this);
        LGPlayerListener playerListener = new LGPlayerListener(this);
        LGBlockListener blockListener = new LGBlockListener(this);
        PluginManager pm = getServer().getPluginManager();

        pm.registerEvent(Event.Type.PLAYER_MOVE, playerListener, Priority.Lowest, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Monitor, this);
        // this will bomb a big map :(
        // pm.registerEvent(Event.Type.CHUNK_LOAD, worldListener, Priority.Lowest, this);
        // not yet in Bukkit/CraftBukkit
        // pm.registerEvent(Event.Type.CHUNK_CREATE, worldListener, Priority.Lowest, this);
        
        monitor = new LongGrassMonitor(this);
        Thread t = new Thread(monitor);
        t.setDaemon(true);
        t.start();
        
        log("enabled");
    }
    
    @Override
    public void onDisable() {
        saveChunkFile();
        
        if (monitor != null) {
            log("Stopping monitor thread");
            monitor.stop();
        }
        
        log("disabled");
    }

    private void loadChunks() {
        
        File chunkCache = getChunksFile();
        if (!chunkCache.exists()) {
            chunks = new WorldSortedLGChunkList(config.getChunkListSize());
            return;
        }
        
        try {
            FileInputStream fis = new FileInputStream(chunkCache);
            ObjectInputStream in = new ObjectInputStream(fis);
            chunks = (WorldSortedLGChunkList) in.readObject();
            in.close();
            fis.close();
            
            while (chunks.size() > config.getChunkListSize()) {
                chunks.remove(0);
            }
            
        } catch (Exception e) {
            log(Level.SEVERE, "Cannot load cached chunks, starting from scratch", e);
            chunks = new WorldSortedLGChunkList(config.getChunkListSize());
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

    public void setGrower(LongGrassGrower grower) {
        this.grower = grower;
    }

    public LongGrassGrower getGrower() {
        return grower;
    }

    public WorldSortedLGChunkList getChunks() {
        return chunks;
    }

    public synchronized void setChunks(List<LGChunk> chunks) {
        this.chunks.removeAll(this.chunks);
        this.chunks.addAll(chunks);
    }
    
    protected LGConfigurationService getConfig() {
        return config;
    }

    public void markMowed(Block block, boolean canRegrow) {
        LGChunk chunk = getLGChunk(block.getChunk(), true);
        if (chunk == null) {
            return;
        }

        debug("mowing " + block + " (" + canRegrow + ")");
        chunk.markMowed(block, canRegrow);
    }

    protected void debug(String string) {
        if (getConfiguration().getBoolean("settings.debug", false)) {
            log(string);
        }
    }

    public LGChunk getLGChunk(Chunk chunk, boolean createIfNotExist) {
        synchronized (chunks) {
            LGChunk lgChunk = new LGChunk(chunk);
            
            if (createIfNotExist && !chunks.contains(lgChunk) && config.isWorldDisabled(lgChunk.getWorld())) {
                chunks.add(lgChunk);
            }
            
            if (chunks.contains(lgChunk)) {
                return chunks.get(chunks.indexOf(lgChunk));
            }
        }
        
        return null;
    }
}
