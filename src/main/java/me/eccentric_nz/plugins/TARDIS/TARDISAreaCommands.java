package me.eccentric_nz.plugins.TARDIS;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TARDISAreaCommands implements CommandExecutor {

    private TARDIS plugin;
    TARDISDatabase service = TARDISDatabase.getInstance();

    public TARDISAreaCommands(TARDIS plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        // If the player typed /tardisarea then do the following...
        // check there is the right number of arguments
        if (cmd.getName().equalsIgnoreCase("tardisarea")) {
            if (args.length == 0) {
                return false;
            }
            if (player == null) {
                sender.sendMessage(plugin.pluginName + ChatColor.RED + " This command can only be run by a player");
                return false;
            }
            if (args[0].equals("start")) {
                // check name is unique and acceptable
                if (args.length < 2 || !args[1].matches("[A-Za-z0-9_]{2,16}")) {
                    sender.sendMessage(plugin.pluginName + "That doesn't appear to be a valid area name (it may be too long)" + ChatColor.GREEN + " /tardisarea start [area_name_goes_here]");
                    return false;
                }
                String queryName = "SELECT area_name FROM areas";
                Statement statement = null;
                ResultSet rsName = null;
                try {
                    Connection connection = service.getConnection();
                    statement = connection.createStatement();
                    rsName = statement.executeQuery(queryName);
                    while (rsName.next()) {
                        if (rsName.getString("area_name").equals(args[1])) {
                            sender.sendMessage(plugin.pluginName + " Area name already in use!");
                            return false;
                        }
                    }
                } catch (SQLException e) {
                    plugin.console.sendMessage(plugin.pluginName + "Couldn't get area names: " + e);
                } finally {
                    if (rsName != null) {
                        try {
                            rsName.close();
                        } catch (Exception e) {
                        }
                    }
                    try {
                        statement.close();
                    } catch (Exception e) {
                    }
                }
                plugin.trackName.put(player.getName(), args[1]);
                player.sendMessage(plugin.pluginName + " Click the area start block to save its position.");
                return true;
            }
            if (args[0].equals("end")) {
                if (!plugin.trackBlock.containsKey(player.getName())) {
                    player.sendMessage(plugin.pluginName + ChatColor.RED + " You haven't selected an area start block!");
                    return false;
                }
                plugin.trackEnd.put(player.getName(), "end");
                player.sendMessage(plugin.pluginName + " Click the area end block to complete the area.");
                return true;
            }
            if (args[0].equals("remove")) {
                String queryRemove = "DELETE FROM areas WHERE area_name = '" + args[1] + "'";
                Statement statement = null;
                try {
                    Connection connection = service.getConnection();
                    statement = connection.createStatement();
                    statement.executeUpdate(queryRemove);
                    player.sendMessage(plugin.pluginName + " Area [" + args[1] + "] deleted!");
                    return true;
                } catch (SQLException e) {
                    plugin.console.sendMessage(plugin.pluginName + "Couldn't delete area: " + e);
                } finally {
                    try {
                        statement.close();
                    } catch (Exception e) {
                    }
                }
            }
            if (args[0].equals("show")) {
                String queryGetArea = "SELECT * FROM areas WHERE area_name = '" + args[1] + "'";
                Statement statement = null;
                try {
                    Connection connection = service.getConnection();
                    statement = connection.createStatement();
                    ResultSet rsArea = statement.executeQuery(queryGetArea);
                    if (!rsArea.next()) {
                        player.sendMessage(plugin.pluginName + "Could not find area [" + args[1] + "]! Did you type the name correctly?");
                        return false;
                    }
                    int mix = rsArea.getInt("minx");
                    int miz = rsArea.getInt("minz");
                    int max = rsArea.getInt("maxx");
                    int maz = rsArea.getInt("maxz");
                    World w = plugin.getServer().getWorld(rsArea.getString("world"));
                    rsArea.close();
                    final Block b1 = w.getHighestBlockAt(mix, miz).getRelative(BlockFace.UP);
                    b1.setTypeId(89);
                    final Block b2 = w.getHighestBlockAt(mix, maz).getRelative(BlockFace.UP);
                    b2.setTypeId(89);
                    final Block b3 = w.getHighestBlockAt(max, miz).getRelative(BlockFace.UP);
                    b3.setTypeId(89);
                    final Block b4 = w.getHighestBlockAt(max, maz).getRelative(BlockFace.UP);
                    b4.setTypeId(89);
                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                        @Override
                        public void run() {
                            b1.setTypeId(0);
                            b2.setTypeId(0);
                            b3.setTypeId(0);
                            b4.setTypeId(0);
                        }
                    }, 300L);
                    return true;
                } catch (SQLException e) {
                    plugin.console.sendMessage(plugin.pluginName + "Couldn't delete area: " + e);
                } finally {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
        return false;
    }
}