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

import eu.over9000.nordic.util.XYZ;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.generator.BlockPopulator;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * BlockPopulator for snake-based caves.
 *
 * @author Pandarr
 *         modified by simplex
 */
public class PopulatorCaves extends BlockPopulator {

	/**
	 * @see org.bukkit.generator.BlockPopulator#populate(org.bukkit.World,
	 * java.util.Random, org.bukkit.Chunk)
	 */
	@Override
	public void populate(final World world, final Random random, final Chunk source) {

		if (random.nextInt(100) < 3) {
			final int x = 4 + random.nextInt(8) + source.getX() * 16;
			final int z = 4 + random.nextInt(8) + source.getZ() * 16;
			int maxY = world.getHighestBlockYAt(x, z);
			if (maxY < 16) {
				maxY = 32;
			}

			final int y = random.nextInt(maxY);
                        final Set<XYZ> snake = selectBlocksForCave(world, random, x, y, z);
                        buildCave(world, snake.toArray(new XYZ[snake.size()]));
		}
	}

	static Set<XYZ> selectBlocksForCave(final World world, final Random random, int blockX, int blockY, int blockZ) {
		final Set<XYZ> snakeBlocks = new HashSet<>();

		int airHits = 0;
		XYZ block = new XYZ();
		while (true) {
			if (airHits > 1200) {
				break;
			}

			if (random.nextInt(20) == 0) {
				blockY++;
                        } else if (world.getBlockAt(blockX, blockY + 2, blockZ).isEmpty()) {
                            blockY += 2;
                        } else if (world.getBlockAt(blockX + 2, blockY, blockZ).isEmpty()) {
                            blockX++;
                        } else if (world.getBlockAt(blockX - 2, blockY, blockZ).isEmpty()) {
                            blockX--;
                        } else if (world.getBlockAt(blockX, blockY, blockZ + 2).isEmpty()) {
                            blockZ++;
                        } else if (world.getBlockAt(blockX, blockY, blockZ - 2).isEmpty()) {
                            blockZ--;
                        } else if (world.getBlockAt(blockX + 1, blockY, blockZ).isEmpty()) {
                            blockX++;
                        } else if (world.getBlockAt(blockX - 1, blockY, blockZ).isEmpty()) {
                            blockX--;
                        } else if (world.getBlockAt(blockX, blockY, blockZ + 1).isEmpty()) {
                            blockZ++;
                        } else if (world.getBlockAt(blockX, blockY, blockZ - 1).isEmpty()) {
                            blockZ--;
			} else if (random.nextBoolean()) {
				if (random.nextBoolean()) {
					blockX++;
				} else {
					blockZ++;
				}
			} else {
				if (random.nextBoolean()) {
					blockX--;
				} else {
					blockZ--;
				}
			}

                        if (!world.getBlockAt(blockX, blockY, blockZ).isEmpty()) {
				final int radius = 1 + random.nextInt(2);
				final int radius2 = radius * radius + 1;
				for (int x = -radius; x <= radius; x++) {
					for (int y = -radius; y <= radius; y++) {
						for (int z = -radius; z <= radius; z++) {
							if (x * x + y * y + z * z <= radius2 && y >= 0 && y < 128) {
                                                                if (world.getBlockAt(blockX + x, blockY + y, blockZ + z).isEmpty()) {
									airHits++;
								} else {
									block.x = blockX + x;
									block.y = blockY + y;
									block.z = blockZ + z;
									if (snakeBlocks.add(block)) {
										block = new XYZ();
									}
								}
							}
						}
					}
				}
			} else {
				airHits++;
			}
		}

		return snakeBlocks;
	}

	static void buildCave(final World world, final XYZ[] snakeBlocks) {
		for (final XYZ loc : snakeBlocks) {
			final Block block = world.getBlockAt(loc.x, loc.y, loc.z);
			if (!block.isEmpty() && !block.isLiquid() && block.getType() != Material.BEDROCK) {
				block.setType(Material.AIR);
			}
		}
	}
}