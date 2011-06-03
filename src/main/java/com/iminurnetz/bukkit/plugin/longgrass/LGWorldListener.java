package com.iminurnetz.bukkit.plugin.longgrass;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.event.world.ChunkCreateEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;

public class LGWorldListener extends WorldListener {

    ArrayList<LGChunk> chunks;
    LongGrassPlugin plugin;
    
    public LGWorldListener(LongGrassPlugin plugin, ArrayList<LGChunk> chunks) {
        this.chunks = chunks;
        this.plugin = plugin;
    }

    @Override
    public void onChunkLoad(ChunkLoadEvent event) {
        plugin.processChunk(event.getChunk());
    }
    
    @Override
    public void onChunkCreate(ChunkCreateEvent event) {
        Chunk chunk = event.getChunk();
        // plugin.log("new chunk created at " + chunk.getX() + "x" + chunk.getZ() + " in world " + chunk.getWorld().getName());
        chunks.add(new LGChunk(chunk));
    }
    
    // not yet in Bukkit/CraftBukkit
    public void onWorldLoad(WorldLoadEvent event) {
        for (Chunk chunk : event.getWorld().getLoadedChunks()) {
            plugin.processChunk(chunk);
        }
    }
}
