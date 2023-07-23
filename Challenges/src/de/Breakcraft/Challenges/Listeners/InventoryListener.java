package de.Breakcraft.Challenges.Listeners;

import de.Breakcraft.Challenges.Challenges.Challenge;
import de.Breakcraft.Challenges.Main;
import de.Breakcraft.Challenges.Utils.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {return;}
        Player p = (Player) e.getWhoClicked();
        if (e.getClickedInventory() == null) {return;}
        if (e.getClickedInventory().getTitle() == null) {return;}
        if ("§c§5Warps".equals(e.getClickedInventory().getTitle())) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null) {return;}
            if (e.getCurrentItem().getType() == Material.STAINED_CLAY) {
                p.closeInventory();
                Bukkit.broadcastMessage(Main.PREFIX + " §aChallenge wird gestartet !");
                Main.getInstance().setChallengeRunning(true);
                Bukkit.broadcastMessage(Main.PREFIX + " §aGeneriere Welt...");
                Main.getInstance().getWorldManager().prepareWorlds();
                Bukkit.broadcastMessage(Main.PREFIX + " §aWelt generiert! Teleportiere...");
                for (Player p2 : Bukkit.getOnlinePlayers()) {
                    p2.teleport(Main.getInstance().getWorldManager().getWorld(WorldManager.Worlds.OVERWORLD).getSpawnLocation());
                }
                Bukkit.getScheduler().cancelTask((Integer) Main.getInstance().getTaskID());
            } else {
                for (Challenge challenge : Main.getInstance().getChallengeManager().challenges) {
                    if (challenge.guiSymbol.getType() == e.getCurrentItem().getType()) {
                        if (challenge.enabled) {
                            challenge.enabled = false;
                            challenge.onChallengeDisable();
                            ItemMeta meta = e.getCurrentItem().getItemMeta();
                            meta.setDisplayName("§a" + challenge.name + " §f[§cDeaktiviert§f]");
                            e.getCurrentItem().setItemMeta(meta);
                        } else {
                            challenge.enabled = true;
                            challenge.onChallengeEnable();
                            ItemMeta meta = e.getCurrentItem().getItemMeta();
                            meta.setDisplayName("§a" + challenge.name + " §f[§aAktiviert§f]");
                            e.getCurrentItem().setItemMeta(meta);
                        }
                    }
                }
            }
        }
    }

}
