package com.iminurnetz.bukkit.plugin.longgrass;

import org.bukkit.Chunk;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.iminurnetz.bukkit.util.TimeOfDay;

public class LGPlayerListener extends PlayerListener {

    private final LongGrassPlugin plugin;

    public LGPlayerListener(LongGrassPlugin plugin) {
        this.plugin = plugin;
    }

    public void onPlayerMove(PlayerMoveEvent event) {
        Chunk chunk = event.getTo().getBlock().getChunk();
        if (TimeOfDay.isDay(chunk.getWorld().getFullTime())) {
            plugin.processChunk(chunk);
        }
    }
}
