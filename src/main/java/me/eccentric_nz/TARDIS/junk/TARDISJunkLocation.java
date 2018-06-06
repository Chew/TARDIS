/*
 * Copyright (C) 2018 eccentric_nz
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

import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.database.ResultSetCurrentLocation;
import me.eccentric_nz.TARDIS.database.ResultSetHomeLocation;
import me.eccentric_nz.TARDIS.database.ResultSetTardisID;
import org.bukkit.Location;
import org.bukkit.block.Biome;

import java.util.HashMap;

/**
 * @author eccentric_nz
 */
public class TARDISJunkLocation {

    private final TARDIS plugin;
    private Location current;
    private Location home;
    private int id;
    private Biome biome;

    public TARDISJunkLocation(TARDIS plugin) {
        this.plugin = plugin;
    }

    public boolean isNotHome() {
        // check the Junk TARDIS is not home already
        ResultSetTardisID rs = new ResultSetTardisID(plugin);
        if (rs.fromUUID("00000000-aaaa-bbbb-cccc-000000000000")) {
            id = rs.getTardis_id();
            // get current location
            HashMap<String, Object> wherec = new HashMap<>();
            wherec.put("tardis_id", id);
            ResultSetCurrentLocation rsc = new ResultSetCurrentLocation(plugin, wherec);
            if (rsc.resultSet()) {
                biome = rsc.getBiome();
                // get home location
                HashMap<String, Object> whereh = new HashMap<>();
                whereh.put("tardis_id", id);
                ResultSetHomeLocation rsh = new ResultSetHomeLocation(plugin, whereh);
                if (rsh.resultSet()) {
                    current = new Location(rsc.getWorld(), rsc.getX(), rsc.getY(), rsc.getZ());
                    home = new Location(rsh.getWorld(), rsh.getX(), rsh.getY(), rsh.getZ());
                    // compare locations
                    return !current.equals(home);
                }
            }
        }
        return true;
    }

    public Location getCurrent() {
        return current;
    }

    public Location getHome() {
        return home;
    }

    public int getId() {
        return id;
    }

    public Biome getBiome() {
        return biome;
    }
}
