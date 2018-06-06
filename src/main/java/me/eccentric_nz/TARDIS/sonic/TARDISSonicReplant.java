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
package me.eccentric_nz.TARDIS.sonic;

import me.eccentric_nz.TARDIS.TARDIS;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Cocoa;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class TARDISSonicReplant implements Runnable {

    private final TARDIS plugin;
    private final Block block;
    private final Block under;
    private final Material type;
    private final Material air = Material.AIR;
    private final Material dirt = Material.DIRT;
    private final Material grass = Material.GRASS_BLOCK;
    private final Material log = Material.JUNGLE_LOG;
    private final Material sand = Material.SAND;
    private final Material soil = Material.FARMLAND;
    private final Material soul = Material.SOUL_SAND;
    private final HashMap<BlockFace, BlockFace> c_data = new HashMap<>();

    public TARDISSonicReplant(TARDIS plugin, Block block, Material type) {
        this.plugin = plugin;
        this.block = block;
        under = block.getRelative(BlockFace.DOWN);
        this.type = type;
        c_data.put(BlockFace.NORTH, BlockFace.SOUTH);
        c_data.put(BlockFace.WEST, BlockFace.EAST);
        c_data.put(BlockFace.SOUTH, BlockFace.NORTH);
        c_data.put(BlockFace.EAST, BlockFace.WEST);
    }

    // TODO check if we need to set the Ageable BlockData for all plant types
    @Override
    public void run() {
        switch (type) {
            case BEETROOT_SEEDS:
                if (under.getType().equals(soil) && block.getType().equals(air)) {
                    block.setType(Material.BEETROOTS);
                } else {
                    block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.BEETROOT));
                }
                break;
            case CARROT:
                if (under.getType().equals(soil) && block.getType().equals(air)) {
                    block.setType(Material.CARROTS);
                } else {
                    block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.CARROT));
                }
                break;
            case COCOA_BEANS:
                if (block.getType().equals(air)) {
                    plugin.getGeneralKeeper().getFaces().forEach((f) -> {
                        // only jungle logs
                        if (block.getRelative(f).getType().equals(log)) {
                            block.setType(Material.COCOA);
                            Cocoa cocoa = (Cocoa) block.getBlockData();
                            cocoa.setFacing(c_data.get(f));
                            cocoa.setAge(0);
                            block.setData(cocoa);
                        }
                    });
                } else {
                    block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.COCOA_BEANS, 1));
                }
                break;
            case MELON_SEEDS:
                if (under.getType().equals(soil) && block.getType().equals(air)) {
                    block.setType(Material.MELON_STEM);
                } else {
                    block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.MELON_SEEDS));
                }
                break;
            case NETHER_WART:
                if (under.getType().equals(soul) && block.getType().equals(air)) {
                    block.setType(Material.NETHER_WART);
                } else {
                    block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.NETHER_WART));
                }
                break;
            case POTATO:
                if (under.getType().equals(soil) && block.getType().equals(air)) {
                    block.setType(Material.POTATOES);
                } else {
                    block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.POTATO));
                }
                break;
            case PUMPKIN_SEEDS:
                if (under.getType().equals(soil) && block.getType().equals(air)) {
                    block.setType(Material.PUMPKIN_STEM);
                } else {
                    block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.PUMPKIN_SEEDS));
                }
                break;
            case SUGAR_CANE:
                if ((under.getType().equals(grass) || under.getType().equals(dirt) || under.getType().equals(sand)) && block.getType().equals(air)) {
                    block.setType(Material.SUGAR_CANE);
                } else {
                    block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.SUGAR_CANE));
                }
                break;
            case WHEAT_SEEDS:
                if (under.getType().equals(soil) && block.getType().equals(air)) {
                    block.setType(Material.WHEAT);
                } else {
                    block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(Material.WHEAT_SEEDS));
                }
                break;
            default:
                break;
        }
    }
}
