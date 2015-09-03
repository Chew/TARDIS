/*
 * Copyright (C) 2015 eccentric_nz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.eccentric_nz.TARDIS.junk;

import java.util.List;
import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.utility.TARDISEffectLibHelper;
import me.eccentric_nz.TARDIS.utility.TARDISSounds;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 *
 * @author eccentric_nz
 */
public class TARDISJunkVortexRunnable implements Runnable {

    private final TARDIS plugin;
    private final Location vortexJunkLoc;
    private final Location destJunkLoc;
    private int i = 0;
    private final int loops = 10;
    private int task;

    public TARDISJunkVortexRunnable(TARDIS plugin, Location vortexJunkLoc, Location destJunkLoc) {
        this.plugin = plugin;
        this.vortexJunkLoc = vortexJunkLoc;
        this.destJunkLoc = destJunkLoc;
    }

    @Override
    public void run() {
        if (i < loops) {
            i++;
            TARDISEffectLibHelper.sendVortexParticles(vortexJunkLoc);
            if (i == 0) {
                // play sound
                for (Entity e : getJunkTravellers()) {
                    if (e instanceof Player) {
                        Player p = (Player) e;
                        TARDISSounds.playTARDISSound(vortexJunkLoc, p, "tardis_junk_vortex");
                    }
                }
            }
            if (i == loops) {
                // teleport players
                for (Entity e : getJunkTravellers()) {
                    if (e instanceof Player) {
                        final Player p = (Player) e;
                        plugin.debug("Player: " + p.getName());
                        final Location relativeLoc = getRelativeLocation(p);
                        p.teleport(relativeLoc);
                        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                p.teleport(relativeLoc);
                            }
                        }, 2L);
                    }
                }
                plugin.getServer().getScheduler().cancelTask(task);
                task = 0;
            }
        }
    }

    private List<Entity> getJunkTravellers() {
        // spawn an entity
        Entity orb = vortexJunkLoc.getWorld().spawnEntity(vortexJunkLoc, EntityType.EXPERIENCE_ORB);
        List<Entity> ents = orb.getNearbyEntities(4.0d, 4.0d, 4.0d);
        orb.remove();
        return ents;
    }

    private Location getRelativeLocation(Player p) {
        Location playerLoc = p.getLocation();
        double x = destJunkLoc.getX() + (playerLoc.getX() - vortexJunkLoc.getX());
        double y = destJunkLoc.getY() + (playerLoc.getY() - vortexJunkLoc.getY()) + 0.5d;
        double z = destJunkLoc.getZ() + (playerLoc.getZ() - vortexJunkLoc.getZ());
        return new Location(destJunkLoc.getWorld(), x, y, z);
    }

    public void setTask(int task) {
        this.task = task;
    }
}
