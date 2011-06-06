package com.iminurnetz.bukkit.plugin.longgrass;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;

public class LGBlockListener extends BlockListener {
    private final LongGrassPlugin plugin;

    public LGBlockListener(LongGrassPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        if (block == null) {
            return;
        }

        if (plugin.getConfig().isPlantOfInterest(block)) {
            plugin.markMowed(block, plugin.getConfig().isUsingTool(player));
        }
    }
}
