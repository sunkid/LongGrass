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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.iminurnetz.bukkit.plugin.util.ConfigurationService;
import com.iminurnetz.bukkit.util.Item;
import com.iminurnetz.bukkit.util.MaterialUtils;

public class LGConfigurationService extends ConfigurationService {

    private static final String LAST_CHANGED_IN_VERSION = "2";

    private static final double DEFAULT_GROW_TIME = 600;
    private static final int DEFAULT_RAIN_FACTOR = 2;
    private static final String DEFAULT_TOOL = "HOE";
    private static final String[] DEFAULT_PLANTS = { "LONG_GRASS", "DEAD_BUSH" };

    private String tool;
    private long growTime;
    private ArrayList<Item> materials;
    private int rainFactor;

    public LGConfigurationService(LongGrassPlugin plugin) {
        super(plugin, LAST_CHANGED_IN_VERSION);
        
        setTool(plugin.getConfiguration().getString("settings.tool", DEFAULT_TOOL));
        setGrowTime((long) plugin.getConfiguration().getDouble("settings.grow-time", DEFAULT_GROW_TIME));
        
        materials = new ArrayList<Item>();
        List<String> plants = plugin.getConfiguration().getStringList("settings.plants", Arrays.asList(DEFAULT_PLANTS));
        for (String plant : plants) {
            try {
                materials.add(new Item(plant));
            } catch (InstantiationException e) {
                plugin.log(Level.SEVERE, "Incorrect item specification: " + plant, e);
            }
        }
        
        rainFactor = plugin.getConfiguration().getInt("rain-factor", DEFAULT_RAIN_FACTOR);
    }

    public boolean isUsingTool(Player player) {
        if (player.getItemInHand() == null) {
            return tool.equals("");
        }
        
        Material playerTool = player.getItemInHand().getType();
        return playerTool.name().endsWith(tool);
    }

    public void setTool(String tool) {
        this.tool = tool.toUpperCase();
    }

    public String getTool() {
        return tool;
    }

    public void setGrowTime(long growTime) {
        this.growTime = growTime;
    }

    public long getGrowTime() {
        return growTime;
    }
    
    public boolean isPlantOfInterest(Block block) {
        return MaterialUtils.isSameMaterial(block.getType(), materials);
    }

    public void setRainFactor(int rainFactor) {
        this.rainFactor = rainFactor;
    }

    public int getRainFactor() {
        return rainFactor;
    }
}
