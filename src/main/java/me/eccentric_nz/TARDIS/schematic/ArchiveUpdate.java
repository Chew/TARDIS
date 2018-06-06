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
package me.eccentric_nz.TARDIS.schematic;

import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.database.TARDISDatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author eccentric_nz
 */
public class ArchiveUpdate {

    private final TARDISDatabaseConnection service = TARDISDatabaseConnection.getINSTANCE();
    private final Connection connection = service.getConnection();
    private final TARDIS plugin;
    private final String uuid;
    private final String name;
    private final String prefix;

    public ArchiveUpdate(TARDIS plugin, String uuid, String name) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.name = name;
        prefix = this.plugin.getPrefix();
    }

    public boolean setInUse() {
        PreparedStatement statement = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT archive_id, name, use FROM " + prefix + "archive WHERE uuid = ?";
        try {
            service.testConnection(connection);
            statement = connection.prepareStatement(query);
            statement.setString(1, uuid);
            rs = statement.executeQuery();
            if (rs.isBeforeFirst()) {
                String update = "UPDATE " + prefix + "archive SET use = ? WHERE archive_id = ?";
                ps = connection.prepareStatement(update);
                while (rs.next()) {
                    int i = 0;
                    if (rs.getString("name").equals(name)) {
                        i = 1;
                    }
                    if (rs.getInt("use") == 1) {
                        i = 2;
                    }
                    ps.setInt(1, i);
                    ps.setInt(2, rs.getInt("archive_id"));
                    ps.executeUpdate();
                }
            } else {
                return false;
            }
        } catch (SQLException e) {
            plugin.debug("ResultSet error for archive update! " + e.getMessage());
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException e) {
                plugin.debug("Error closing archive update! " + e.getMessage());
            }
        }
        return true;
    }
}
