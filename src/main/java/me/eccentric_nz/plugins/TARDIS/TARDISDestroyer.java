package me.eccentric_nz.plugins.TARDIS;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;

public class TARDISDestroyer {

    private final TARDIS plugin;
    TARDISDatabase service = TARDISDatabase.getInstance();

    public TARDISDestroyer(TARDIS plugin) {
        this.plugin = plugin;
    }

    public final void destroyTARDIS(TARDISConstants.SCHEMATIC schm, int id, World w, TARDISConstants.COMPASS d, int i, String p) {
        short h, width, l;
        switch (schm) {
            case BIGGER:
                h = plugin.biggerdimensions[0];
                width = plugin.biggerdimensions[1];
                l = plugin.biggerdimensions[2];
                break;
            case DELUXE:
                h = plugin.deluxedimensions[0];
                width = plugin.deluxedimensions[1];
                l = plugin.deluxedimensions[2];
                break;
            default:
                h = plugin.budgetdimensions[0];
                width = plugin.budgetdimensions[1];
                l = plugin.budgetdimensions[2];
                break;
        }
        // inner TARDIS
        int level, row, col, x, y, z, startx, starty = (14 + h), startz, resetx, resetz;
        // calculate startx, starty, startz
        int gsl[] = plugin.utils.getStartLocation(id, d);
        startx = gsl[0];
        resetx = gsl[1];
        startz = gsl[2];
        resetz = gsl[3];
        x = gsl[4];
        z = gsl[5];
        for (level = 0; level < h; level++) {
            for (row = 0; row < width; row++) {
                for (col = 0; col < l; col++) {
                    // set the block to stone
                    Block b = w.getBlockAt(startx, starty, startz);
                    Material m = b.getType();
                    // if it's a chest clear the inventory first
                    if (m.equals(Material.CHEST)) {
                        Chest container = (Chest) b.getState();
                        //Is it a double chest?
                        Chest chest = getDoubleChest(b);
                        if (chest != null) {
                            chest.getInventory().clear();
                            if (chest.getBlock().setTypeId(i) && container.getBlock().setTypeId(i)) {
                                plugin.debug("Deleted both halves of the double chest");
                            }
                        } else if (container != null) {
                            container.getInventory().clear();
                            if (container.getBlock().setTypeId(i)) {
                                plugin.debug("Deleted the single chest");
                            }
                        }
                    }
                    // if it's a furnace clear the inventory first
                    if (m == Material.FURNACE) {
                        Furnace fur = (Furnace) b.getState();
                        fur.getInventory().clear();
                    }
                    if (m != Material.CHEST) {
                        plugin.utils.setBlock(w, startx, starty, startz, i, (byte) 0);
                    }
                    startx += x;
                }
                startx = resetx;
                startz += z;
            }
            startz = resetz;
            starty -= 1;
        }
        if (plugin.worldGuardOnServer) {
            plugin.wgchk.removeRegion(w, p);
        }
    }

    public void destroyBlueBox(Location l, TARDISConstants.COMPASS d, int id, boolean hide) {
        World w = l.getWorld();
        int sbx = l.getBlockX() - 1;
        int rbx = sbx;
        int gbx = sbx;
        int sby = l.getBlockY();
        int sbz = l.getBlockZ() - 1;
        int rbz = sbz;
        int gbz = sbz;
        // remove blue wool and door
        for (int yy = 0; yy < 3; yy++) {
            for (int xx = 0; xx < 3; xx++) {
                for (int zz = 0; zz < 3; zz++) {
                    plugin.utils.setBlock(w, sbx, sby, sbz, 0, (byte) 0);
                    sbx++;
                }
                sbx = rbx;
                sbz++;
            }
            sbz = rbz;
            sby++;
        }
        // replace the block under the door if there is one
        Statement statement = null;
        ResultSet rs = null;
        try {
            Connection connection = service.getConnection();
            statement = connection.createStatement();
            String queryReplaced = "SELECT replaced FROM tardis WHERE tardis_id = '" + id + "' LIMIT 1";
            rs = statement.executeQuery(queryReplaced);
            if (rs.next()) {
                String replacedData = rs.getString("replaced");
                if (!replacedData.equals("")) {
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
                    Block b = rw.getBlockAt(rx, ry, rz);
                    b.setTypeIdAndData(rID, rb, true);
                }
            }
            // finally forget the replaced block
            String queryForget = "UPDATE tardis SET replaced = '' WHERE tardis_id = " + id;
            statement.executeUpdate(queryForget);

            // get rid of platform is there is one
            if (plugin.getConfig().getBoolean("platform")) {
                String queryPlatform = "SELECT platform FROM tardis WHERE tardis_id = " + id;
                ResultSet prs = statement.executeQuery(queryPlatform);
                if (prs.next()) {
                    String plat = prs.getString("platform");
                    if (!prs.wasNull() && !plat.equals("")) {
                        int px = 0, py = 0, pz = 0;
                        String[] str_blocks = prs.getString("platform").split("~");
                        for (String sb : str_blocks) {
                            String[] p_data = sb.split(":");
                            World pw = plugin.getServer().getWorld(p_data[0]);
                            Material mat = Material.valueOf(p_data[4]);
                            try {
                                px = Integer.valueOf(p_data[1]);
                                py = Integer.valueOf(p_data[2]);
                                pz = Integer.valueOf(p_data[3]);
                            } catch (NumberFormatException nfe) {
                                plugin.console.sendMessage(plugin.pluginName + "Could not convert to number!");
                            }
                            Block pb = pw.getBlockAt(px, py, pz);
                            pb.setType(mat);
                        }
                    }
                    // forget the platform blocks
                    String queryEmptyP = "UPDATE tardis SET platform = '' WHERE tardis_id = " + id;
                    statement.executeUpdate(queryEmptyP);
                }
                prs.close();
            }
            // check protected blocks if has block id and data stored then put the block back!
            String queryGetBlocks = "SELECT * FROM blocks WHERE tardis_id = " + id;
            ResultSet rsBlocks = statement.executeQuery(queryGetBlocks);
            while (rsBlocks.next()) {
                int bID = rsBlocks.getInt("block");
                if (bID != 0) {
                    byte data = rsBlocks.getByte("data");
                    String locStr = rsBlocks.getString("location");
                    String[] loc_data = locStr.split(",");
                    // x, y, z - 1, 2, 3
                    String[] xStr = loc_data[1].split("=");
                    String[] yStr = loc_data[2].split("=");
                    String[] zStr = loc_data[3].split("=");
                    int rx = plugin.utils.parseNum(xStr[1].substring(0, (xStr[1].length() - 2)));
                    int ry = plugin.utils.parseNum(yStr[1].substring(0, (yStr[1].length() - 2)));
                    int rz = plugin.utils.parseNum(zStr[1].substring(0, (zStr[1].length() - 2)));
                    plugin.utils.setBlock(w, rx, ry, rz, bID, data);
                }
            }
            rsBlocks.close();
            // remove protected blocks from the blocks table
            if (hide == false) {
                String queryRemoveBlocks = "DELETE FROM blocks WHERE tardis_id = " + id;
                statement.executeUpdate(queryRemoveBlocks);
            }
        } catch (SQLException e) {
            plugin.console.sendMessage(plugin.pluginName + " Save Replaced Block Error: " + e);
        } finally {
            try {
                rs.close();
            } catch (Exception e) {
            }
            try {
                statement.close();
            } catch (Exception e) {
            }
        }
    }

    public void destroySign(Location l, TARDISConstants.COMPASS d) {
        World w = l.getWorld();
        int signx = 0, signz = 0;
        switch (d) {
            case EAST:
                signx = -2;
                signz = 0;
                break;
            case SOUTH:
                signx = 0;
                signz = -2;
                break;
            case WEST:
                signx = 2;
                signz = 0;
                break;
            case NORTH:
                signx = 0;
                signz = 2;
                break;
        }
        int signy = 2;
        plugin.utils.setBlock(w, l.getBlockX() + signx, l.getBlockY() + signy, l.getBlockZ() + signz, 0, (byte) 0);
    }

    public void destroyTorch(Location l) {
        World w = l.getWorld();
        int tx = l.getBlockX();
        int ty = l.getBlockY() + 3;
        int tz = l.getBlockZ();
        plugin.utils.setBlock(w, tx, ty, tz, 0, (byte) 0);
    }
    //Originally stolen from Babarix. Thank you :)

    public Chest getDoubleChest(Block block) {
        Chest chest = null;
        if (block.getRelative(BlockFace.NORTH).getTypeId() == 54) {
            chest = (Chest) block.getRelative(BlockFace.NORTH).getState();
            return chest;
        } else if (block.getRelative(BlockFace.EAST).getTypeId() == 54) {
            chest = (Chest) block.getRelative(BlockFace.EAST).getState();
            return chest;
        } else if (block.getRelative(BlockFace.SOUTH).getTypeId() == 54) {
            chest = (Chest) block.getRelative(BlockFace.SOUTH).getState();
            return chest;
        } else if (block.getRelative(BlockFace.WEST).getTypeId() == 54) {
            chest = (Chest) block.getRelative(BlockFace.WEST).getState();
            return chest;
        }
        return chest;
    }
}
