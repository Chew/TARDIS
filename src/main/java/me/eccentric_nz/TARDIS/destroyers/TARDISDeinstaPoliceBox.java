/*
 * Copyright (C) 2013 eccentric_nz
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
package me.eccentric_nz.TARDIS.destroyers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.TARDISConstants;
import me.eccentric_nz.TARDIS.database.QueryFactory;
import me.eccentric_nz.TARDIS.database.ResultSetBlocks;
import me.eccentric_nz.TARDIS.database.ResultSetPlayerPrefs;
import me.eccentric_nz.TARDIS.database.ResultSetTardis;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * A police box is a telephone kiosk that can be used by members of the public
 * wishing to get help from the police. Early in the First Doctor's travels, the
 * TARDIS assumed the exterior shape of a police box during a five-month
 * stopover in 1963 London. Due a malfunction in its chameleon circuit, the
 * TARDIS became locked into that shape.
 *
 * @author eccentric_nz
 */
public class TARDISDeinstaPoliceBox {

    private final TARDIS plugin;

    public TARDISDeinstaPoliceBox(TARDIS plugin) {
        this.plugin = plugin;
    }

    /**
     * Destroys the TARDIS Police Box. A 3 x 3 x 3 block area.
     *
     * @param l the location of the TARDIS Police Box (bottom centre).
     * @param d the direction the Police Box is facing.
     * @param id the unique key of the record for this TARDIS in the database.
     * @param hide boolean determining whether to forget the protected Police
     * Box blocks.
     */
    @SuppressWarnings("deprecation")
    public void instaDestroyPB(Location l, TARDISConstants.COMPASS d, int id, boolean hide) {
        World w = l.getWorld();
        // make sure chunk is loaded
        Chunk chunk = w.getChunkAt(l);
        while (!chunk.isLoaded()) {
            chunk.load();
        }
        int sbx = l.getBlockX() - 1;
        int sby = l.getBlockY();
        int sbz = l.getBlockZ() - 1;
        // remove torch
//        plugin.destroyPB.destroyTorch(l);
        // remove sign
        plugin.destroyPB.destroySign(l, d);
        // remove blue wool and door
        for (int yy = 0; yy < 4; yy++) {
            for (int xx = 0; xx < 3; xx++) {
                for (int zz = 0; zz < 3; zz++) {
                    Block b = w.getBlockAt((sbx + xx), (sby + yy), (sbz + zz));
                    if (!b.getType().equals(Material.AIR)) {
                        b.setType(Material.AIR);
                    }
                }
            }
        }
        // replace the block under the door if there is one
        HashMap<String, Object> where = new HashMap<String, Object>();
        where.put("tardis_id", id);
        ResultSetTardis rs = new ResultSetTardis(plugin, where, "", false);
        QueryFactory qf = new QueryFactory(plugin);
        String owner;
        Block b;
        if (rs.resultSet()) {
            owner = rs.getOwner();
            String replacedData = rs.getReplaced();
            if (!replacedData.isEmpty()) {
                String[] parts = replacedData.split(":");
                World rw = plugin.getServer().getWorld(parts[0]);
                int rx, ry, rz, rID;
                byte rb = 0;
                rx = plugin.utils.parseNum(parts[1]);
                ry = plugin.utils.parseNum(parts[2]);
                rz = plugin.utils.parseNum(parts[3]);
                rID = plugin.utils.parseNum(parts[4]);
                try {
                    rb = Byte.valueOf(parts[5]);
                } catch (NumberFormatException nfe) {
                    plugin.console.sendMessage(plugin.pluginName + "Could not convert to number!");
                }
                b = rw.getBlockAt(rx, ry, rz);
                b.setTypeIdAndData(rID, rb, true);
                HashMap<String, Object> wherepp = new HashMap<String, Object>();
                wherepp.put("player", owner);
                ResultSetPlayerPrefs rsp = new ResultSetPlayerPrefs(plugin, wherepp);
                if (rsp.resultSet()) {
                    boolean sub = (rsp.isSubmarine_on() && plugin.trackSubmarine.contains(Integer.valueOf(id)));
                    if (sub && plugin.worldGuardOnServer) {
                        plugin.wgutils.sponge(b, true);
                    }
                }
            }
        }
        // finally forget the replaced block
        HashMap<String, Object> set = new HashMap<String, Object>();
        HashMap<String, Object> wherer = new HashMap<String, Object>();
        wherer.put("tardis_id", id);
        set.put("replaced", "");
        qf.doUpdate("tardis", set, wherer);

        // get rid of platform is there is one
        if (plugin.getConfig().getBoolean("platform")) {
            String plat = rs.getPlatform();
            plugin.destroyPB.destroyPlatform(plat, id);
        }
        // check protected blocks if has block id and data stored then put the block back!
        HashMap<String, Object> tid = new HashMap<String, Object>();
        tid.put("tardis_id", id);
        ResultSetBlocks rsb = new ResultSetBlocks(plugin, tid, true);
        if (rsb.resultSet()) {
            ArrayList<HashMap<String, String>> data = rsb.getData();
            for (HashMap<String, String> map : data) {
                int bID = 0;
                if (map.get("block") != null) {
                    bID = plugin.utils.parseNum(map.get("block"));
                }
                if (bID != 0) {
                    byte bd = Byte.parseByte(map.get("data"));
                    String locStr = map.get("location");
                    String[] loc_data = locStr.split(",");
                    // x, y, z - 1, 2, 3
                    String[] xStr = loc_data[1].split("=");
                    String[] yStr = loc_data[2].split("=");
                    String[] zStr = loc_data[3].split("=");
                    int rx = plugin.utils.parseNum(xStr[1].substring(0, (xStr[1].length() - 2)));
                    int ry = plugin.utils.parseNum(yStr[1].substring(0, (yStr[1].length() - 2)));
                    int rz = plugin.utils.parseNum(zStr[1].substring(0, (zStr[1].length() - 2)));
                    plugin.utils.setBlock(w, rx, ry, rz, bID, bd);
                }
            }
        }
        if (hide == false) {
            HashMap<String, Object> whereb = new HashMap<String, Object>();
            whereb.put("tardis_id", id);
            whereb.put("police_box", 1);
            qf.doDelete("blocks", whereb);
            // remove from protectBlockMap - remove(Integer.valueOf(id)) would only remove the first one
            plugin.protectBlockMap.values().removeAll(Collections.singleton(Integer.valueOf(id)));
        }
        plugin.tardisDematerialising.remove(Integer.valueOf(id));
        plugin.tardisChunkList.remove(l.getChunk());
    }
}
