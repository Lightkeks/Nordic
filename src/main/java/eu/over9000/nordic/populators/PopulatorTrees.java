/*
 * Copyright 2012 s1mpl3x
 * 
 * This file is part of Nordic.
 * 
 * Nordic is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Nordic is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Nordic If not, see <http://www.gnu.org/licenses/>.
 */
package eu.over9000.nordic.populators;

import org.bukkit.*;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class PopulatorTrees extends BlockPopulator {

       @Override
       public void populate(final World world, final Random ignored, final Chunk source) {
               final int chunkX = source.getX() << 4;
               final int chunkZ = source.getZ() << 4;

               // seed based random for natural distribution across chunks
               final long worldSeed = world.getSeed();
               final Random seededRandom = new Random(worldSeed + source.getX() * 341873128712L + source.getZ() * 132897987541L);

               // spawn small spruce trees across all heights
               final int treeCount = 7 + seededRandom.nextInt(8); // 7-14 trees per chunk

               for (int i = 0; i < treeCount; i++) {
                       final int x = chunkX + seededRandom.nextInt(32) - 8;
                       final int z = chunkZ + seededRandom.nextInt(32) - 8;

                       if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                               continue;
                       }

                       final Block top = world.getHighestBlockAt(x, z);
                       final Material type = top.getType();
                       if (top.isLiquid() || (type != Material.GRASS_BLOCK && type != Material.PODZOL)) {
                               continue;
                       }

                       final Location loc = top.getLocation().add(0, 1, 0);
                       world.generateTree(loc, TreeType.REDWOOD);
               }
       }

}
