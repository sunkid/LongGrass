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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

public class WorldSortedLGChunkList extends ArrayList<LGChunk> implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final Comparator<LGChunk> comparator = new Comparator<LGChunk>() {
        @Override
        public int compare(LGChunk c1, LGChunk c2) {
            return c1.getWorld().compareTo(c2.getWorld());
        }};
        
    @Override
    public boolean add(LGChunk chunk) {
        this.add(size(), chunk);
        return true;
    }
    
    @Override
    public void add(int index, LGChunk chunk) {
        boolean isSorted = true;
        if (index > 0 && !get(index - 1).getWorld().equals(chunk.getWorld())) {
            isSorted = false;
        }
        
        super.add(index, chunk);
        
        if (!isSorted) {
            sort();
        }
    }
    
    @Override
    public boolean addAll(Collection<? extends LGChunk> chunks) {
        super.addAll(chunks);
        sort();
        return true;
    }
    
    @Override
    public boolean addAll(int index, Collection<? extends LGChunk> chunks) {
        super.addAll(index, chunks);
        sort();
        return true;
    }

    private void sort() {
        Collections.sort(this, comparator);
    }
}
