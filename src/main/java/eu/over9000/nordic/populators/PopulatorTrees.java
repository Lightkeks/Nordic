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
import org.bukkit.block.BlockFace;
import org.bukkit.generator.BlockPopulator;

import java.util.Random;

public class PopulatorTrees extends BlockPopulator {

        @Override
        public void populate(final World world, final Random random, final Chunk source) {
                // spawn large spruce trees with a wider random range
                final int largeTrees = 6 + random.nextInt(8); // 6-13
                for (int t = 0; t < largeTrees; t++) {
                        final int treeX = random.nextInt(15) + source.getX() * 16;
                        final int treeZ = random.nextInt(15) + source.getZ() * 16;

                        final Block ground = findGround(world, treeX, treeZ);
                        final Material groundType = ground.getType();
                        if (groundType == Material.WATER || groundType == Material.LAVA) {
                                continue;
                        }
                        if (groundType != Material.GRASS_BLOCK && groundType != Material.DIRT) {
                                ground.setType(Material.GRASS_BLOCK, false);
                        }

                        final Location high = ground.getRelative(BlockFace.UP).getLocation();
                        if (random.nextBoolean()) {
                                world.generateTree(high, TreeType.TALL_REDWOOD);
                        } else {
                                world.generateTree(high, TreeType.REDWOOD);
                        }
                }

                // spawn many small spruce trees everywhere, with more variety
                final int smallTrees = 30 + random.nextInt(25); // 30-54
                for (int t = 0; t < smallTrees; t++) {
                        final int treeX = random.nextInt(15) + source.getX() * 16;
                        final int treeZ = random.nextInt(15) + source.getZ() * 16;

                        final Block ground = findGround(world, treeX, treeZ);
                        final Material groundType = ground.getType();
                        if (groundType == Material.WATER || groundType == Material.LAVA) {
                                continue;
                        }
                        if (groundType != Material.GRASS_BLOCK && groundType != Material.DIRT) {
                                ground.setType(Material.GRASS_BLOCK, false);
                        }
                        final Location high = ground.getRelative(BlockFace.UP).getLocation();
                        final TreeType type = random.nextInt(5) == 0 ? TreeType.TALL_REDWOOD : TreeType.REDWOOD;
                        world.generateTree(high, type); // smaller spruce variant
                }
        }

        private Block findGround(final World world, final int x, final int z) {
                Block block = world.getHighestBlockAt(x, z);
                while (block.getY() > world.getMinHeight() && isTreeMaterial(block.getType())) {
                        block = block.getRelative(BlockFace.DOWN);
                }
                return block;
        }

        private boolean isTreeMaterial(final Material mat) {
                return mat.name().endsWith("_LOG") || mat.name().endsWith("_LEAVES");
        }
}
