package me.eccentric_nz.TARDIS.builders;

import me.eccentric_nz.TARDIS.TARDIS;
import me.eccentric_nz.TARDIS.TARDISConstants;
import me.eccentric_nz.TARDIS.enumeration.SCHEMATIC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class TARDISTimeRotor {

    private static final HashMap<String, Integer> BY_NAME = new HashMap<String, Integer>() {
        {
            put("early", 10000002);
            put("rotor", 10000003);
            put("copper", 10000004);
            put("round", 10000005);
        }
    };

    public static void setItemFrame(SCHEMATIC schm, Location location, int id) {
        location.getBlock().setBlockData(TARDISConstants.VOID_AIR);
        ItemFrame itemFrame = (ItemFrame) location.getWorld().spawnEntity(location, EntityType.ITEM_FRAME);
        itemFrame.setFacingDirection(BlockFace.UP);
        setRotor(BY_NAME.get(schm.getPermission()), itemFrame, false);
        // save itemFrame UUID
        updateRotorRecord(id, itemFrame.getUniqueId().toString());
    }

    public static void updateRotorRecord(int id, String uuid) {
        HashMap<String, Object> where = new HashMap<>();
        where.put("tardis_id", id);
        HashMap<String, Object> set = new HashMap<>();
        set.put("rotor", uuid);
        TARDIS.plugin.getQueryFactory().doUpdate("tardis", set, where);
    }

    public static void setRotor(int which, ItemFrame itemFrame, boolean animated) {
        Material material = (animated) ? Material.LIGHT_BLUE_DYE : Material.LIGHT_GRAY_DYE;
        ItemStack is = new ItemStack(material, 1);
        ItemMeta im = is.getItemMeta();
        im.setDisplayName("Time Rotor");
        im.setCustomModelData(which);
        is.setItemMeta(im);
        itemFrame.setItem(is);
    }

    public static ItemFrame getItemFrame(UUID uuid) {
        ItemFrame itemFrame = (ItemFrame) Bukkit.getEntity(uuid);
        return itemFrame;
    }

    public static int getRotorModelData(ItemFrame itemFrame) {
        ItemStack is = itemFrame.getItem();
        if (is.hasItemMeta()) {
            ItemMeta im = is.getItemMeta();
            if (im.hasCustomModelData()) {
                return im.getCustomModelData();
            }
        }
        return 10000002;
    }
}
