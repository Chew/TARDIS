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
package me.eccentric_nz.TARDIS.chameleon;

import java.util.Arrays;
import me.eccentric_nz.TARDIS.TARDIS;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author eccentric_nz
 */
public class TARDISChameleonConstructorGUI {

    private final TARDIS plugin;
    private final ItemStack[] construct;

    public TARDISChameleonConstructorGUI(TARDIS plugin) {
        this.plugin = plugin;
        this.construct = getItemStack();
    }

    private ItemStack[] getItemStack() {

        ItemStack[] is = new ItemStack[54];

        // back
        ItemStack back = new ItemStack(Material.ARROW, 1);
        ItemMeta bk = back.getItemMeta();
//        bk.setDisplayName(plugin.getLanguage().getString("Back"));
        bk.setDisplayName("Back to presets");
        back.setItemMeta(bk);
        is[0] = back;
        // help
        ItemStack help = new ItemStack(Material.BOWL, 1);
        ItemMeta hp = help.getItemMeta();
//        hp.setDisplayName(plugin.getLanguage().getString("Help"));
        hp.setDisplayName("Help");
        help.setItemMeta(hp);
        is[3] = help;
        // info
        ItemStack info = new ItemStack(Material.BOWL, 1);
        ItemMeta io = info.getItemMeta();
//        hp.setDisplayName(plugin.getLanguage().getString("Help"));
        io.setDisplayName("Info");
        io.setLore(Arrays.asList("Click the lamp slot to switch", "between all available lamps.", "Click the door slots tp switch", "between all available doors."));
        info.setItemMeta(io);
        is[4] = info;
        // abort
        ItemStack abort = new ItemStack(Material.BOWL, 1);
        ItemMeta at = abort.getItemMeta();
//        at.setDisplayName(plugin.getLanguage().getString("Close"));
        at.setDisplayName("Abort ");
        abort.setItemMeta(at);
        is[6] = abort;
        // save button
        ItemStack save = new ItemStack(Material.BOWL, 1);
        ItemMeta se = save.getItemMeta();
//        se.setDisplayName(plugin.getLanguage().getString("Save construction"));
        se.setDisplayName("Save construction");
        save.setItemMeta(se);
        is[8] = save;
        // lamp button
        ItemStack lamp = new ItemStack(Material.TORCH, 1);
        is[26] = lamp;
        // save button
        ItemStack door = new ItemStack(Material.IRON_DOOR, 1);
        is[43] = door;
        is[52] = door;

        return is;
    }

    public ItemStack[] getConstruct() {
        return construct;
    }
}