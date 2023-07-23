package de.Breakcraft.Challenges.Utils;

import de.Breakcraft.Challenges.Main;
import de.Breakcraft.Challenges.Utils.config.ConfigManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.io.File;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

public class WorldManager implements Listener {

	HashMap<Worlds, World> worlds = new HashMap<>();

	private final String lobby;
	private final String overworld;
	private final String nether;
	private final String end;
	private long seed = new Random().nextLong();

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public void randomizeSeed() {
		this.seed = new Random().nextLong();
	}

	public enum Worlds {
		LOBBY,
		OVERWORLD,
		NETHER,
		END;
	}

	public World getWorld(Worlds world) {
		if(world == null) return null;
		else return worlds.get(world);
	}

	public WorldManager(String lobby, String overworld, String nether, String end, long seed) {
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
		this.lobby = lobby;
		this.overworld = overworld;
		this.nether = nether;
		this.end = end;
		this.seed = seed;
		prepareWorlds();
	}

	public WorldManager() {
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
		this.lobby = Main.getInstance().getConfigManager().get(ConfigManager.ConfigPath.WORLDS_LOBBY);
		this.overworld =
				Main.getInstance().getConfigManager().get(ConfigManager.ConfigPath.WORLDS_OVERWORLD);
		this.nether = Main.getInstance().getConfigManager().get(ConfigManager.ConfigPath.WORLDS_NETHER);
		this.end = Main.getInstance().getConfigManager().get(ConfigManager.ConfigPath.WORLDS_END);
		randomizeSeed();
		prepareWorlds();
	}


	public void prepareWorlds() {
		for (String s : Bukkit.getWorlds().stream().map(World::getName).collect(Collectors.toList())) {
			if (s.equalsIgnoreCase("lobby")) {
				deleteWorld(Bukkit.getWorld(s));
			}
		}
		worlds = new HashMap<Worlds, World>() {{
			if (Bukkit.getWorld(lobby) == null)
				put(Worlds.LOBBY,
						new WorldCreator(lobby).type(WorldType.FLAT).environment(World.Environment.NORMAL).generateStructures(false).seed(seed).createWorld());
			else put(Worlds.LOBBY, Bukkit.getWorld(lobby));
			put(Worlds.OVERWORLD,
					new WorldCreator(overworld).type(WorldType.NORMAL).environment(World.Environment.NORMAL).seed(seed).createWorld());
			put(Worlds.NETHER,
					new WorldCreator(nether).type(WorldType.NORMAL).environment(World.Environment.NETHER).seed(seed).createWorld());
			put(Worlds.END,
					new WorldCreator(end).type(WorldType.NORMAL).environment(World.Environment.THE_END).seed(seed).createWorld());
		}};
	}

	public void deleteWorld(World world) {
		if(world == null) return;
		for (Player player : world.getPlayers()) {
			player.teleport(getWorld(Worlds.LOBBY).getSpawnLocation());
			player.setHealth(20);
			player.setFoodLevel(20);
			player.setExp(0);
			player.setLevel(0);
		}

		Bukkit.unloadWorld(world, false);
		deleteFolder(world.getWorldFolder());
	}

	private void deleteFolder(File file) {
		for(File file2 : file.listFiles()) {
			if(file2.isDirectory()) {
				deleteFolder(file2);
			} else file2.delete();
		}
		file.delete();
	}

	public void deleteGameWorld() {
		deleteWorld(worlds.get(Worlds.OVERWORLD));
		deleteWorld(worlds.get(Worlds.NETHER));
		deleteWorld(worlds.get(Worlds.END));
		randomizeSeed();
	}

	@EventHandler
	public void onPortalPort(PlayerPortalEvent event) {
		Player player = event.getPlayer();

		if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
			event.useTravelAgent(true);
			event.getPortalTravelAgent().setCanCreatePortal(true);
			Location toLocation;
			if (player.getWorld() == worlds.get(Worlds.OVERWORLD)) {
				toLocation = new Location(worlds.get(Worlds.NETHER), event.getFrom().getBlockX() / 8,
						event.getFrom().getBlockY(), event.getFrom().getBlockZ() / 8);
			} else {
				toLocation = new Location(worlds.get(Worlds.OVERWORLD), event.getFrom().getBlockX() * 8,
						event.getFrom().getBlockY(), event.getFrom().getBlockZ() * 8);
			}
			event.setTo(event.getPortalTravelAgent().findOrCreate(toLocation));
		} else if (event.getCause() == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
			if (player.getWorld() == worlds.get(Worlds.OVERWORLD)) {
				Location toLoc = new Location(worlds.get(Worlds.END), 100, 50, 0); // location für obsidian plattform
				event.setTo(toLoc);
				Block block = toLoc.getBlock();
				for (int x = block.getX() - 2; x <= block.getX() + 2; x++) {
					for (int z = block.getZ() - 2; z <= block.getZ() + 2; z++) {
						Block platformBlock = toLoc.getWorld().getBlockAt(x, block.getY() - 1, z);
						if (platformBlock.getType() != Material.OBSIDIAN) {
							platformBlock.setType(Material.OBSIDIAN);
						}
						for (int yMod = 1; yMod <= 3; yMod++) {
							Block b = platformBlock.getRelative(BlockFace.UP, yMod);
							if (b.getType() != Material.AIR) {
								b.setType(Material.AIR);
							}
						}
					}
				}
			} else if (player.getWorld() == worlds.get(Worlds.END)) {
				event.setTo(worlds.get(Worlds.OVERWORLD).getSpawnLocation());
			}
		}
	}

}
