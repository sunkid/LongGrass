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

import org.bukkit.block.Block;

public class LGCoordinate implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final int x;
    private final int y;
    private final int z;
    
    public LGCoordinate(Block block) {
        this(block.getX(), block.getY(), block.getZ());
    }

    public LGCoordinate(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public int getZ() {
        return z;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LGCoordinate)) {
            return false;
        }
        
        LGCoordinate that = (LGCoordinate) o;
        
        if (that.x == this.x && that.y == this.y && this.z == that.z) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public int hashCode() {
        return (37 * x + y) * 31 + z;
    }
}
