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

/**
 * Daemon thread to periodically re-grow plants.
 * @author <a href="mailto:sunkid@iminurnetz.com">sunkid</a>
 *
 */
public class LongGrassMonitor implements Runnable {

    private static final long INTERVAL = 10000;
    private final LongGrassPlugin plugin;
    private boolean stopped;
    
    public LongGrassMonitor(LongGrassPlugin plugin) {
        this.plugin = plugin;
        this.stopped = false;
    }
    
    @Override
    public void run() {
        // an initial snooze
        try {
            Thread.sleep(INTERVAL);
        } catch (InterruptedException e) {
            // ignored
        }

        while (!stopped) {
            plugin.getGrower().regrowPlants();
            
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                // ignored
            }
        }
        
        plugin.log("Monitor thread stopped!");
    }

    public void stop() {
        stopped = true;
    }

}
