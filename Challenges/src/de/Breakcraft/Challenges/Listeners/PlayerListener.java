package de.Breakcraft.Challenges.Listeners;

import de.Breakcraft.Challenges.Challenges.Challenge;
import de.Breakcraft.Challenges.Main;
import de.Breakcraft.Challenges.Utils.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {
	public static List<Player> startedPlayers = new ArrayList<>();
	private boolean challengeResetting = false;

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if (!(e.getEntity() instanceof Player)) {return;}
		Player p = (Player) e.getEntity();
		if (p.getLocation().getWorld().equals(Main.getInstance().getWorldManager().getWorld(WorldManager.Worlds.LOBBY))) e.setCancelled(true);
		else {
			if (!Main.getInstance().isChallengeRunning()) {return;}
			if (!((p.getHealth() - e.getDamage()) <= 0)) {return;}
			e.setCancelled(true);
			p.setGameMode(GameMode.SPECTATOR);
			for (ItemStack itemStack : p.getInventory().getContents()) {
				if (itemStack != null) {
					p.getLocation().getWorld().dropItem(p.getLocation(), itemStack);
				}
			}
			p.getInventory().clear();
			int count = 0;
			for (Player p2 : startedPlayers) {
				if (p2.getGameMode() == GameMode.SPECTATOR) count++;
			}
			if (count != startedPlayers.size()) {return;}
			if(!challengeResetting) {
				challengeResetting = true;
				Bukkit.broadcastMessage(Main.PREFIX + " §aChallenge wurde erfolglos beendet !");
				Bukkit.broadcastMessage(Main.PREFIX + " §aModi wird in 5 Sekunden resettet !");
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
					@Override
					public void run() {
						for (Challenge challenge : Main.getInstance().getChallengeManager().challenges) {
							if (challenge.enabled) {
								challenge.enabled = false;
								challenge.onChallengeDisable();
							}
						}
						for (Player p : Bukkit.getOnlinePlayers()) p.getInventory().clear();
						Main.getInstance().getWorldManager().deleteGameWorld();
						Bukkit.broadcastMessage(Main.PREFIX + " §aWelten wurden gelöscht und Challenges " +
								"deaktiviert !");
						Main.getInstance().setChallengeRunning(false);
						Main.getInstance().getTimer().reset();
						Main.getInstance().startChangeScheduler();
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (Main.getInstance().getForcedSpawn() != null) {
								p.teleport(Main.getInstance().getForcedSpawn());
							} else {
								p.teleport(Main.getInstance().getWorldManager().getWorld(WorldManager.Worlds.LOBBY).getSpawnLocation());
							}
							p.setGameMode(GameMode.SURVIVAL);
						}
						challengeResetting = false;
					}
				}, 20 * 5);
			}
		}
	}

	@EventHandler
	public void onHungerChangeEvent(FoodLevelChangeEvent e) {
		if (!(e.getEntity() instanceof Player)) {return;}
		Player p = (Player) e.getEntity();
		if (p.getLocation().getWorld().getName().equals(Main.getInstance().getWorldManager().getWorld(WorldManager.Worlds.LOBBY).getName()))
			e.setCancelled(true);
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (!p.getLocation().getWorld().getName().equals(Main.getInstance().getWorldManager().getWorld(WorldManager.Worlds.LOBBY).getName())) {
			return;
		}
		if (!(p.hasPermission("breakcraft.lobby.interact"))) e.setCancelled(true);
	}

	@EventHandler
	public void onDeath(EntityDeathEvent e) {
		if (e.getEntity() instanceof EnderDragon) {
			if(!challengeResetting) {
				challengeResetting = true;
				Bukkit.broadcastMessage(Main.PREFIX + " §aChallenge wurde mit Erfolg beendet !");
				Bukkit.broadcastMessage(Main.PREFIX + " §aModi wird in 5 Sekunden resettet !");
				Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
					@Override
					public void run() {
						for (Challenge challenge : Main.getInstance().getChallengeManager().challenges) {
							if (challenge.enabled) {
								challenge.enabled = false;
								challenge.onChallengeDisable();
							}
						}
						for (Player p : Bukkit.getOnlinePlayers()) p.getInventory().clear();
						Main.getInstance().getWorldManager().deleteGameWorld();
						Bukkit.broadcastMessage(Main.PREFIX + " §aWelten wurden gelöscht und Challenges deaktiviert !");
						Main.getInstance().setChallengeRunning(false);
						Main.getInstance().getTimer().reset();
						Main.getInstance().startChangeScheduler();
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (Main.getInstance().getForcedSpawn() != null) {
								p.teleport(Main.getInstance().getForcedSpawn());
							} else {
								p.teleport(Main.getInstance().getWorldManager().getWorld(WorldManager.Worlds.LOBBY).getSpawnLocation());
							}
							p.setGameMode(GameMode.SURVIVAL);
						}
						challengeResetting = false;
					}
				}, 20 * 5);
			}
		}
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		final String prefix = PermissionsEx.getUser(e.getPlayer()).getPrefix().replace('&', '§');
		e.setFormat(prefix + " " + e.getPlayer().getName() + " : §f" + e.getMessage());
	}

}
