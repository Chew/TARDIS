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
package me.eccentric_nz.TARDIS.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.database.QueryFactory;
import me.eccentric_nz.TARDIS.database.ResultSetDestinations;
import me.eccentric_nz.TARDIS.database.ResultSetTardis;
import me.eccentric_nz.TARDIS.database.ResultSetTravellers;
import me.eccentric_nz.TARDIS.thirdparty.Version;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * TARDISes could fly through space like conventional spacecraft, but doing so
 * for prolonged periods could damage the ship, at least in the case of the
 * Doctor's TARDIS.
 *
 * @author eccentric_nz
 */
public class TARDISBindListener implements Listener {

    private final TARDIS plugin;
    List<Material> validBlocks = new ArrayList<Material>();
    Version bukkitversion;
    Version prewoodbuttonversion = new Version("1.4.2");

    public TARDISBindListener(TARDIS plugin) {
        this.plugin = plugin;
        String[] v = Bukkit.getServer().getBukkitVersion().split("-");
        bukkitversion = new Version(v[0]);

        validBlocks.add(Material.WALL_SIGN);
        validBlocks.add(Material.SIGN_POST);
        if (bukkitversion.compareTo(prewoodbuttonversion) >= 0) {
            validBlocks.add(Material.WOOD_BUTTON);
        }
        validBlocks.add(Material.STONE_BUTTON);
        validBlocks.add(Material.LEVER);
    }

    /**
     * Listens for player interaction with blocks after running the /tardisbind
     * [save|cmd|player|area] command. If the player's name is contained in the
     * trackBinder HashMap then the block location is recorded in the bind field
     * of the destinations table.
     *
     * If the player is travelling in the TARDIS then a check is made of the
     * destinations table for the location of the clicked block. If found the
     * destination for the next TARDIS time travel location is set.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent event) {
        Block b = event.getClickedBlock();
        if (b != null) {
            Material m = b.getType();
            if (validBlocks.contains(m)) {
                final Player player = event.getPlayer();
                String playerNameStr = player.getName();
                String l = b.getLocation().toString();
                HashMap<String, Object> where = new HashMap<String, Object>();
                if (plugin.trackBinder.containsKey(playerNameStr)) {
                    where.put("dest_id", plugin.trackBinder.get(playerNameStr));
                    plugin.trackBinder.remove(playerNameStr);
                    HashMap<String, Object> set = new HashMap<String, Object>();
                    set.put("bind", l);
                    QueryFactory qf = new QueryFactory(plugin);
                    qf.doUpdate("destinations", set, where);
                    player.sendMessage(plugin.pluginName + "Save successfully bound to " + m.toString());
                } else {
                    // is player travelling in TARDIS
                    where.put("player", playerNameStr);
                    ResultSetTravellers rst = new ResultSetTravellers(plugin, where, false);
                    if (rst.resultSet()) {
                        int id = rst.getTardis_id();
                        plugin.utils.updateTravellerCount(id);
                        // check they have enough artron energy to travel
                        HashMap<String, Object> wheret = new HashMap<String, Object>();
                        wheret.put("tardis_id", id);
                        ResultSetTardis rs = new ResultSetTardis(plugin, wheret, "", false);
                        if (rs.resultSet()) {
                            HashMap<String, Object> whereb = new HashMap<String, Object>();
                            whereb.put("tardis_id", id);
                            whereb.put("bind", l);
                            ResultSetDestinations rsd = new ResultSetDestinations(plugin, whereb, false);
                            if (rsd.resultSet()) {
                                if (!rs.isHandbrake_on()) {
                                    player.sendMessage(plugin.pluginName + ChatColor.RED + "You cannot set a destination while the TARDIS is travelling!");
                                    return;
                                }
                                // what bind type is it?
                                int type = rsd.getType();
                                String dest_name = rsd.getDest_name();
                                switch (type) {
                                    case 1: // command
                                        if (dest_name.equals("rebuild")) {
                                            player.performCommand("tardis rebuild");
                                            plugin.console.sendMessage(player.getName() + " issued server command: /tardis rebuild");
                                        }
                                        if (dest_name.equals("hide")) {
                                            player.performCommand("tardis hide");
                                            plugin.console.sendMessage(player.getName() + " issued server command: /tardis hide");
                                        }
                                        if (dest_name.equals("home")) {
                                            player.performCommand("tardistravel home");
                                            plugin.console.sendMessage(player.getName() + " issued server command: /tardistravel home");
                                        }
                                        break;
                                    case 2: // player
                                        player.performCommand("tardistravel " + dest_name);
                                        plugin.console.sendMessage(player.getName() + " issued server command: /tardistravel " + dest_name);
                                        break;
                                    case 3: // area
                                        player.performCommand("tardistravel area " + dest_name);
                                        plugin.console.sendMessage(player.getName() + " issued server command: /tardistravel area " + dest_name);
                                        break;
                                    default: // save
                                        player.performCommand("tardistravel dest " + dest_name);
                                        plugin.console.sendMessage(player.getName() + " issued server command: /tardistravel dest " + dest_name);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
