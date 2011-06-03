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

import org.bukkit.Chunk;

public class LGChunk implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final int x;
    private final int z;
    private final String world;
    
    public LGChunk(Chunk chunk) {
        this.x = chunk.getX();
        this.z = chunk.getZ();
        this.world = chunk.getWorld().getName();
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
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
}
