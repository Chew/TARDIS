package me.eccentric_nz.plugins.TARDIS;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TARDISPrefsCommands implements CommandExecutor {

    private TARDIS plugin;
    TARDISDatabase service = TARDISDatabase.getInstance();

    public TARDISPrefsCommands(TARDIS plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        // If the player typed /tardisprefs then do the following...
        // check there is the right number of arguments
        if (cmd.getName().equalsIgnoreCase("tardisprefs")) {
            if (args.length == 0) {
                return false;
            }
            if (player == null) {
                sender.sendMessage(plugin.pluginName + ChatColor.RED + " This command can only be run by a player");
                return false;
            }
            if (args[0].equalsIgnoreCase("sfx")) {
                if (player.hasPermission("tardis.timetravel")) {
                    if (args.length < 2 || (!args[1].equalsIgnoreCase("on") && !args[1].equalsIgnoreCase("off"))) {
                        sender.sendMessage(plugin.pluginName + " You need to specify if sound effects should be on or off!");
                        return false;
                    }
                    // get the players sfx setting
                    Statement statement = null;
                    ResultSet rs = null;
                    try {
                        Connection connection = service.getConnection();
                        statement = connection.createStatement();
                        String querySFX = "SELECT * FROM player_prefs WHERE player = '" + player.getName() + "'";
                        rs = statement.executeQuery(querySFX);
                        if (rs == null || !rs.next()) {
                            String queryInsert = "INSERT INTO player_prefs (player) VALUES ('" + player.getName() + "')";
                            statement.executeUpdate(queryInsert);
                        }
                        if (args[1].equalsIgnoreCase("on")) {
                            String queryUpdate = "UPDATE player_prefs SET sfx_on = 1 WHERE player = '" + player.getName() + "'";
                            statement.executeUpdate(queryUpdate);
                            sender.sendMessage(plugin.pluginName + " Sound effects were turned ON!");
                        }
                        if (args[1].equalsIgnoreCase("off")) {
                            String queryUpdate = "UPDATE player_prefs SET sfx_on = 0 WHERE player = '" + player.getName() + "'";
                            statement.executeUpdate(queryUpdate);
                            sender.sendMessage(plugin.pluginName + " Sound effects were turned OFF.");
                        }
                        return true;
                    } catch (SQLException e) {
                        TARDIS.plugin.console.sendMessage(plugin.pluginName + " SFX Preferences Save Error: " + e);
                    } finally {
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (Exception e) {
                            }
                        }
                        try {
                            statement.close();
                        } catch (Exception e) {
                        }
                    }
                } else {
                    sender.sendMessage(plugin.pluginName + TARDISConstants.NO_PERMS_MESSAGE);
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("platform")) {
                if (player.hasPermission("tardis.timetravel")) {
                    if (args.length < 2 || (!args[1].equalsIgnoreCase("on") && !args[1].equalsIgnoreCase("off"))) {
                        sender.sendMessage(plugin.pluginName + " You need to specify if sound effects should be on or off!");
                        return false;
                    }
                    // get the players platform setting
                    Statement statement = null;
                    ResultSet rs = null;
                    try {
                        Connection connection = service.getConnection();
                        statement = connection.createStatement();
                        String queryPlatform = "SELECT * FROM player_prefs WHERE player = '" + player.getName() + "'";
                        rs = statement.executeQuery(queryPlatform);
                        if (rs == null || !rs.next()) {
                            String queryInsert = "INSERT INTO player_prefs (player) VALUES ('" + player.getName() + "')";
                            statement.executeUpdate(queryInsert);
                        }
                        if (args[1].equalsIgnoreCase("on")) {
                            String queryUpdate = "UPDATE player_prefs SET platform_on = 1 WHERE player = '" + player.getName() + "'";
                            statement.executeUpdate(queryUpdate);
                            sender.sendMessage(plugin.pluginName + " The safety platform was turned ON!");
                        }
                        if (args[1].equalsIgnoreCase("off")) {
                            String queryUpdate = "UPDATE player_prefs SET platform_on = 0 WHERE player = '" + player.getName() + "'";
                            statement.executeUpdate(queryUpdate);
                            sender.sendMessage(plugin.pluginName + " safety platform was turned OFF.");
                        }
                        return true;
                    } catch (SQLException e) {
                        TARDIS.plugin.console.sendMessage(plugin.pluginName + " Platform Preferences Save Error: " + e);
                    } finally {
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (Exception e) {
                            }
                        }
                        try {
                            statement.close();
                        } catch (Exception e) {
                        }
                    }
                } else {
                    sender.sendMessage(plugin.pluginName + TARDISConstants.NO_PERMS_MESSAGE);
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("quotes")) {
                if (player.hasPermission("tardis.timetravel")) {
                    if (args.length < 2 || (!args[1].equalsIgnoreCase("on") && !args[1].equalsIgnoreCase("off"))) {
                        sender.sendMessage(plugin.pluginName + " You need to specify if Who quotes should be on or off!");
                        return false;
                    }
                    // get the players quotes setting
                    Statement statement = null;
                    ResultSet rs = null;
                    try {
                        Connection connection = service.getConnection();
                        statement = connection.createStatement();
                        String queryPlatform = "SELECT * FROM player_prefs WHERE player = '" + player.getName() + "'";
                        rs = statement.executeQuery(queryPlatform);
                        if (rs == null || !rs.next()) {
                            String queryInsert = "INSERT INTO player_prefs (player) VALUES ('" + player.getName() + "')";
                            statement.executeUpdate(queryInsert);
                        }
                        if (args[1].equalsIgnoreCase("on")) {
                            String queryUpdate = "UPDATE player_prefs SET quotes_on = 1 WHERE player = '" + player.getName() + "'";
                            statement.executeUpdate(queryUpdate);
                            sender.sendMessage(plugin.pluginName + " Quotes were turned ON!");
                        }
                        if (args[1].equalsIgnoreCase("off")) {
                            String queryUpdate = "UPDATE player_prefs SET quotes_on = 0 WHERE player = '" + player.getName() + "'";
                            statement.executeUpdate(queryUpdate);
                            sender.sendMessage(plugin.pluginName + " Quotes were turned OFF.");
                        }
                        return true;
                    } catch (SQLException e) {
                        TARDIS.plugin.console.sendMessage(plugin.pluginName + " Quotes Preferences Save Error: " + e);
                    } finally {
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (Exception e) {
                            }
                        }
                        try {
                            statement.close();
                        } catch (Exception e) {
                        }
                    }
                } else {
                    sender.sendMessage(plugin.pluginName + TARDISConstants.NO_PERMS_MESSAGE);
                    return false;
                }
            }
        }
        return false;
    }
}