package eu.over9000.nordic;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.ChunkGenerator;

/**
 * Listener to detect worlds that load after the plugin was started.
 * If a world uses the Nordic generator we log its presence.
 */
public class MVWorldLoadListener implements Listener {

    private final Nordic plugin;

    public MVWorldLoadListener(final Nordic plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldLoad(final WorldLoadEvent event) {
        final World world = event.getWorld();
        final ChunkGenerator gen = world.getGenerator();
        if (gen instanceof NordicChunkGenerator) {
            plugin.getLogger().info("[Nordic] Detected loaded Nordic world: " + world.getName());
        }
    }
}
