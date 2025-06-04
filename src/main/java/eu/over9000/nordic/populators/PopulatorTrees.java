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
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class PopulatorTrees extends BlockPopulator {

       @Override
       public void populate(final World world, final Random random, final Chunk source) {
               final int chunkX = source.getX() << 4;
               final int chunkZ = source.getZ() << 4;

               // use a seed based random for more natural distribution across chunks
               final long worldSeed = world.getSeed();
               final Random seeded = new Random(worldSeed + source.getX() * 341873128712L + source.getZ() * 132897987541L);

               final int largeTrees = 3 + seeded.nextInt(4); // 3-6
               for (int t = 0; t < largeTrees; t++) {
                       final int x = chunkX + seeded.nextInt(24) - 4;
                       final int z = chunkZ + seeded.nextInt(24) - 4;

                       if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                               continue;
                       }
                       final int y = world.getHighestBlockYAt(x, z);
                       final Block ground = world.getBlockAt(x, y - 1, z);
                       final Material groundType = ground.getType();
                       if (groundType == Material.WATER || groundType == Material.LAVA) {
                               continue;
                       }
                       if (groundType != Material.GRASS_BLOCK && groundType != Material.DIRT) {
                               ground.setType(Material.GRASS_BLOCK, false);
                       }

                       generateSpruceTree(world, seeded, ground, true);
               }

               final int smallTrees = 15 + seeded.nextInt(10); // 15-24
               for (int t = 0; t < smallTrees; t++) {
                       final int x = chunkX + seeded.nextInt(24) - 4;
                       final int z = chunkZ + seeded.nextInt(24) - 4;

                       if (!world.isChunkLoaded(x >> 4, z >> 4)) {
                               continue;
                       }
                       final int y = world.getHighestBlockYAt(x, z);
                       final Block ground = world.getBlockAt(x, y - 1, z);
                       final Material groundType = ground.getType();
                       if (groundType == Material.WATER || groundType == Material.LAVA) {
                               continue;
                       }
                       if (groundType != Material.GRASS_BLOCK && groundType != Material.DIRT) {
                               ground.setType(Material.GRASS_BLOCK, false);
                       }

                       generateSpruceTree(world, seeded, ground, seeded.nextInt(5) == 0);
               }
       }

        private void generateSpruceTree(final World world, final Random random, final Block ground, final boolean tall) {
                final int height = tall ? 8 + random.nextInt(5) : 5 + random.nextInt(3);
                // trunk
                for (int y = 1; y <= height; y++) {
                        final Block trunk = ground.getRelative(0, y, 0);
                        if (trunk.isEmpty() || trunk.isLiquid()) {
                                trunk.setType(Material.SPRUCE_LOG, false);
                        }
                }

                int leafStart = height - 3;
                for (int y = leafStart; y <= height; y++) {
                        int radius = y == height ? 1 : 2;
                        for (int x = -radius; x <= radius; x++) {
                                for (int z = -radius; z <= radius; z++) {
                                        if (Math.abs(x) + Math.abs(z) > radius + 1) {
                                                continue;
                                        }
                                        final Block leaf = ground.getRelative(x, y, z);
                                        if (leaf.isEmpty()) {
                                                leaf.setType(Material.SPRUCE_LEAVES, false);
                                        }
                                }
                        }
                }
        }

}
