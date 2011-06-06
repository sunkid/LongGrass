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

import java.io.Serializable;
import java.util.Date;
import java.util.Hashtable;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

import com.iminurnetz.bukkit.util.SerializableBlockType;

public class LGChunk implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final int x;
    private final int z;
    private final String world;
    
    private final Hashtable<LGCoordinate, Date> blockLastMowed;
    private Date lastMowed;
    private final Hashtable<LGCoordinate, SerializableBlockType> plantMaterial;

    public LGChunk(Chunk chunk) {
        this(chunk.getX(), chunk.getZ(), chunk.getWorld().getName());
    }

    public LGChunk(int x, int z, String world) {
        this.x = x;
        this.z = z;
        this.world = world;
        blockLastMowed = new Hashtable<LGCoordinate, Date>();
        plantMaterial = new Hashtable<LGCoordinate, SerializableBlockType>();
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
    
    public String getWorld() {
        return world;
    }

    public Hashtable<LGCoordinate, Date> getBlockLastMowed() {
        return blockLastMowed;
    }

    public Hashtable<LGCoordinate, SerializableBlockType> getPlantMaterial() {
        return plantMaterial;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LGChunk)) {
            return false;
        }
        
        LGChunk that = (LGChunk) o;
        
        if (that.x == this.x && that.z == this.z && this.world.equals(that.world)) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return (37 * x + z) * 31 + world.hashCode();
    }

    public void markMowed(Block block) {
        markMowed(block, false);
    }

    public void markMowed(Block block, boolean canRegrow) {
        LGCoordinate coord = new LGCoordinate(block);
        if (canRegrow) {
            lastMowed = new Date();
            blockLastMowed.put(coord, lastMowed);
            plantMaterial.put(coord, new SerializableBlockType(block));
        } else {
            blockLastMowed.remove(coord);
            lastMowed = null;
            for (LGCoordinate c : blockLastMowed.keySet()) {
                if (lastMowed == null) {
                    lastMowed = blockLastMowed.get(c);
                    continue;
                }

                if (lastMowed.before(blockLastMowed.get(c))) {
                    lastMowed = blockLastMowed.get(c);
                }
            }
        }
    }
    
    public Date getLastMowed() {
        return lastMowed;
    }

    public void setLastMowed(Date date) {
        lastMowed = date;
    }
}
