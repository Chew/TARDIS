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
package me.eccentric_nz.TARDIS.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.eccentric_nz.TARDIS.TARDIS;
import org.jnbt.ByteArrayTag;
import org.jnbt.CompoundTag;
import org.jnbt.NBTInputStream;
import org.jnbt.ShortTag;
import org.jnbt.Tag;

/**
 * The Abzorbaloff was an alien who disguised himself as a human under the alias
 * Victor Kennedy. He could absorb the bodies of his victims, along with their
 * memories and consciousness, into himself at a simple touch.
 *
 * @author eccentric_nz
 */
public class TARDISRoomSchematicReader {

    private TARDIS plugin;

    public TARDISRoomSchematicReader(TARDIS plugin) {
        this.plugin = plugin;
    }

    private static Tag getChildTag(Map<String, Tag> items, String key, Class<? extends Tag> expected) {
        Tag tag = items.get(key);
        return tag;
    }

    /**
     * Reads a WorldEdit schematic file and writes the data to a CSV file. The
     * dimensions of the schematics are also stored for use by the room builder.
     */
    public void readAndMakeCSV(String fileStr, String s, boolean rotate) {
        plugin.console.sendMessage(plugin.pluginName + "Loading schematic: " + fileStr + ".schematic");
        FileInputStream fis = null;
        try {
            File f = new File(fileStr + ".schematic");
            fis = new FileInputStream(f);
            NBTInputStream nbt = new NBTInputStream(fis);
            CompoundTag backuptag = (CompoundTag) nbt.readTag();
            Map<String, Tag> tagCollection = backuptag.getValue();

            short width = (Short) getChildTag(tagCollection, "Width", ShortTag.class).getValue();
            short height = (Short) getChildTag(tagCollection, "Height", ShortTag.class).getValue();
            short length = (Short) getChildTag(tagCollection, "Length", ShortTag.class).getValue();

            short[] dimensions = new short[3];
            dimensions[0] = height;
            dimensions[1] = width;
            dimensions[2] = length;
            plugin.room_dimensions.put(s, dimensions);

            byte[] blocks = (byte[]) getChildTag(tagCollection, "Blocks", ByteArrayTag.class).getValue();
            byte[] data = (byte[]) getChildTag(tagCollection, "Data", ByteArrayTag.class).getValue();

            nbt.close();
            fis.close();
            int i = 0;
            String[] blockdata = new String[width * height * length];
            for (byte b : blocks) {
                blockdata[i] = b + ":" + data[i];
                i++;
            }
            int j = 0;
            List<String[][]> layers = new ArrayList<String[][]>();
            for (int h = 0; h < height; h++) {
                String[][] strarr = new String[width][length];
                for (int w = 0; w < width; w++) {
                    for (int l = 0; l < length; l++) {
                        strarr[w][l] = blockdata[j];
                        j++;
                    }
                }
                if (rotate) {
                    strarr = rotateSquareCCW(strarr);
                }
                layers.add(strarr);
            }
            try {
                String csvFile = (rotate) ? fileStr + "_EW.csv" : fileStr + ".csv";
                File file = new File(csvFile);
                BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
                for (String[][] l : layers) {
                    for (String[] lines : l) {
                        StringBuilder buf = new StringBuilder();
                        for (String bd : lines) {
                            buf.append(bd).append(",");
                        }
                        String commas = buf.toString();
                        String strCommas = commas.substring(0, (commas.length() - 1));
                        bw.write(strCommas);
                        bw.newLine();
                    }
                }
                bw.close();
            } catch (IOException io) {
                plugin.console.sendMessage(plugin.pluginName + "Could not save the time lords file!");
            }
        } catch (IOException e) {
            plugin.console.sendMessage(plugin.pluginName + "Schematic read error: " + e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                }
            }
        }
    }

//    /**
//     * Rotates a square 2D array 90 degrees clockwise.
//     */
//    private static String[][] rotateSquareCW(String[][] mat) {
//        final int size = mat.length;
//        String[][] out = new String[size][size];
//        for (int r = 0; r < size; r++) {
//            for (int c = 0; c < size; c++) {
//                out[c][size - 1 - r] = mat[r][c];
//            }
//        }
//        return out;
//    }
    /**
     * Rotates a square 2D array 90 degrees counterclockwise. This is used for
     * the (non-symmetrical) TARDIS passage ways so that they are built
     * correctly in the EAST and WEST directions.
     */
    private String[][] rotateSquareCCW(String[][] mat) {
        int size = mat.length;
        String[][] out = new String[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                out[r][c] = mat[c][size - r - 1];
            }
        }
        return out;
    }
}
