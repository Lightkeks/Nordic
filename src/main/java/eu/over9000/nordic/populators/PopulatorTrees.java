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
                // spawn large spruce trees but with a lower base amount
                final int largeTrees = 3 + random.nextInt(4); // 3-6
                for (int t = 0; t < largeTrees; t++) {
                        final int relX = 3 + random.nextInt(10);
                        final int relZ = 3 + random.nextInt(10);

                        final Block ground = findGround(source, relX, relZ);
                        final Material groundType = ground.getType();
                        if (groundType == Material.WATER || groundType == Material.LAVA) {
                                continue;
                        }
                        if (groundType != Material.GRASS_BLOCK && groundType != Material.DIRT) {
                                ground.setType(Material.GRASS_BLOCK, false);
                        }

                        generateSpruceTree(world, random, ground, true);
                }

                // spawn small spruce trees in a reduced quantity
                final int smallTrees = 15 + random.nextInt(10); // 15-24
                for (int t = 0; t < smallTrees; t++) {
                        final int relX = 3 + random.nextInt(10);
                        final int relZ = 3 + random.nextInt(10);

                        final Block ground = findGround(source, relX, relZ);
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

        private Block findGround(final Chunk chunk, final int relX, final int relZ) {
                final World world = chunk.getWorld();
                int y = world.getMaxHeight() - 1;
                Block block = chunk.getBlock(relX, y, relZ);
                while (block.getY() > world.getMinHeight() && isTreeMaterial(block.getType())) {
                        block = block.getRelative(BlockFace.DOWN);
                }
                return block;
        }

        private boolean isTreeMaterial(final Material mat) {
                return mat.name().endsWith("_LOG") || mat.name().endsWith("_LEAVES");
        }
}
