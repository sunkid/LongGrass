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
