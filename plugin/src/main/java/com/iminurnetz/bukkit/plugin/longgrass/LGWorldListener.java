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

import org.bukkit.Chunk;
import org.bukkit.event.world.ChunkCreateEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;

public class LGWorldListener extends WorldListener {

    private final LongGrassPlugin plugin;
    
    public LGWorldListener(LongGrassPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onChunkLoad(ChunkLoadEvent event) {
        plugin.getGrower().populateChunk(event.getChunk());
    }
    
    @Override
    public void onChunkCreate(ChunkCreateEvent event) {
        Chunk chunk = event.getChunk();
        // plugin.log("new chunk created at " + chunk.getX() + "x" + chunk.getZ() + " in world " + chunk.getWorld().getName());
        plugin.getChunks().add(new LGChunk(chunk));
    }
    
    // not yet in Bukkit/CraftBukkit
    public void onWorldLoad(WorldLoadEvent event) {
        for (Chunk chunk : event.getWorld().getLoadedChunks()) {
            plugin.getGrower().populateChunk(chunk);
        }
    }
}
