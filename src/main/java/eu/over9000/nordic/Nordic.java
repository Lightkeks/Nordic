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
package eu.over9000.nordic;

import eu.over9000.nordic.populators.*;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;

import eu.over9000.nordic.MVWorldLoadListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Main class of Nordic
 *
 * @author simplex
 */
public class Nordic extends JavaPlugin {
        private NordicChunkGenerator wgen;
        private boolean logChunkGenTime;

	private static final String DEFAULT_WORLD_NAME = "world_nordic";
    private static final String WORLD_PREFIX = "world_";
    private List<BlockPopulator> populators;

       @Override
       public void onLoad() {
               // nothing to do here yet
       }

        public boolean isLogChunkGenTime() {
                return logChunkGenTime;
        }

	@Override
	public void onDisable() {
	}

        @Override
        public void onEnable() {
                saveDefaultConfig();
                logChunkGenTime = getConfig().getBoolean("logChunkGenTime", false);
                // Register a listener to detect late world loads (e.g. via Multiverse)
                getServer().getPluginManager().registerEvents(new MVWorldLoadListener(this), this);
                getLogger().info("[Nordic] Plugin v0.3.1-CODEX initialized");
                if (!getServer().getWorlds().isEmpty()) {
                        getLogger().info("[Nordic] Detected world: " + getServer().getWorlds().get(0).getName());
                }
                if (getServer().getPluginManager().isPluginEnabled("Multiverse-Core")) {
                        getLogger().info("[Nordic] Multiverse-Core detected – checking for world import consistency.");
                }
                populators = buildPopulators(this);
                wgen = new NordicChunkGenerator(populators, this);
                getLogger().info("[Nordic] Assigned default generator: " + wgen.getClass().getSimpleName());
                getLogger().info("[Nordic] Registered 0 biome(s), " + populators.size() + " structure(s)");

                // Check already loaded worlds for the Nordic generator
                for (final World world : getServer().getWorlds()) {
                        final ChunkGenerator gen = world.getGenerator();
                        if (gen instanceof NordicChunkGenerator) {
                                getLogger().info("[Nordic] Found active Nordic world: " + world.getName());
                        }
                }
        }

	@Override
	public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("player only command");
			return true;
		}
		final Player player = (Player) sender;
		if (!player.hasPermission("nordic.command")) {
			player.sendMessage("You don't have the permission required to use this plugin");
			return true;
		}
		if (command.getName().equalsIgnoreCase("nordic")) {
			String worldName;
			final long seed;

			switch (args.length) {
				case 0:  // /nordic
					seed = ThreadLocalRandom.current().nextLong();
					worldName = DEFAULT_WORLD_NAME;
					break;
				case 1: // /nordic WORLD_NAME
					seed = ThreadLocalRandom.current().nextLong();
					worldName = args[0];
					break;
				case 2: // /nordic WORLD_NAME SEED
					seed = buildSeed(args[1]);
					worldName = args[0];
					break;
				default:
					player.sendMessage("Syntax: /nordic <WORLD_NAME> <SEED>");
					return false;
			}

			if (!worldName.startsWith(WORLD_PREFIX)) {
				worldName = WORLD_PREFIX + worldName;
			}

                        player.sendMessage(ChatColor.BLUE + "[Nordic] Generating/loading world " + ChatColor.WHITE + worldName + ChatColor.BLUE + " with seed " + ChatColor.WHITE + seed + ChatColor.BLUE + "...");
                        final World world = WorldCreator.name(worldName).environment(Environment.NORMAL).seed(seed).generator(wgen).createWorld();
                        getLogger().info("[Nordic] " + player.getName() + " created/loaded world: " + worldName + " with seed " + world.getSeed());

			player.sendMessage(ChatColor.BLUE + "[Nordic] done, teleporting to spawn of the generated world");
			player.teleport(world.getSpawnLocation());

			return true;
		}
		return false;
	}

	/**
	 * Build a List of all Populators
	 *
	 * @return a ArrayList<BlockPopulator> that contains all populators
	 */
    private static List<BlockPopulator> buildPopulators(final Plugin plugin) {
            final ArrayList<BlockPopulator> populators = new ArrayList<>();
            populators.add(new PopulatorLakes());
            populators.add(new PopulatorGravel());
            populators.add(new PopulatorLavaLakes());
            populators.add(new PopulatorCaves());
            populators.add(new PopulatorOres());
            populators.add(new PopulatorCustomTrees());
            populators.add(new PopulatorTrees(plugin));
            populators.add(new PopulatorFlowers());
            populators.add(new PopulatorMushrooms());
            populators.add(new PopulatorLonggrass());
            populators.add(new PopulatorSnow());
            return populators;
    }

	/**
	 * Builds a seed from a string
	 *
	 * @param s seed user input
	 * @return long seed
	 */
	private long buildSeed(final String s) {
		try {
			return Long.parseLong(s);
		} catch (final NumberFormatException e) {
			return s.hashCode();
		}
	}

        @Override
        public ChunkGenerator getDefaultWorldGenerator(final String worldName, final String id) {
                if (worldName == null || worldName.isEmpty()) {
                        getLogger().warning("[Nordic] \u26A0\uFE0F Unknown world requested: '" + worldName + "' – falling back to default generator.");
                        return null;
                }

                if (!DEFAULT_WORLD_NAME.equals(worldName)) {
                        getLogger().info("[Nordic] Ignoring unrelated world: " + worldName);
                        return null;
                }

                getLogger().info("[Nordic] getDefaultWorldGenerator(" + worldName + ")");

                if (wgen == null) {
                        wgen = new NordicChunkGenerator(populators, this);
                }
                return wgen;
        }

}

