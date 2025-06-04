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

                        generateSpruceTree(world, random, ground, true);
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

                        generateSpruceTree(world, random, ground, random.nextInt(5) == 0);
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
