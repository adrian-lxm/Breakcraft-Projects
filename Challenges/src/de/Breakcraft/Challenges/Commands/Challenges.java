package de.Breakcraft.Challenges.Commands;

import de.Breakcraft.Challenges.Challenges.Challenge;
import de.Breakcraft.Challenges.Listeners.PlayerListener;
import de.Breakcraft.Challenges.Main;
import de.Breakcraft.Challenges.Utils.WorldManager;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Challenges implements CommandExecutor {
    public static HashMap<UUID, Location> lastLocations = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length == 0) {
                if(!(Main.getInstance().isChallengeRunning())) {
                    if(p.getUniqueId().equals(Main.getInstance().getCurrentHoster())) {
                        Inventory inv;
                        int size;
                        if (Main.getInstance().getChallengeManager().challenges.size() < 7) {
                            size = 1;
                            inv = Bukkit.createInventory(null, 3 * 9, "§c§5Warps");
                        } else {
                            if(Main.getInstance().getChallengeManager().challenges.size() % 7 == 0) {
                                inv = Bukkit.createInventory(null, (Main.getInstance().getChallengeManager().challenges.size() / 7 + 3) * 9, "§c§5Warps");
                                size = Main.getInstance().getChallengeManager().challenges.size() / 7;
                            } else {
                                double var1 = Main.getInstance().getChallengeManager().challenges.size() / 7;
                                if(Math.round(var1) > var1) {
                                    inv = Bukkit.createInventory(null, (((int) Math.round(var1) + 2) * 9), "§c§5Warps");
                                    size = (int) Math.round(var1);
                                } else {
                                    inv = Bukkit.createInventory(null, (((int) Math.round(var1) + 3) * 9), "§c§5Warps");
                                    size = (int) (Math.round(var1) + 1);
                                }
                            }
                        }
                        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1,(short) 0,  DyeColor.GRAY.getDyeData());
                        ItemMeta meta = glass.getItemMeta();
                        meta.setDisplayName("");
                        meta.addEnchant(Enchantment.FIRE_ASPECT, 1, false);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        glass.setItemMeta(meta);

                        for(int i = 0; i < 10; ++i) {
                            inv.setItem(i, glass);
                        }

                        List<Integer> intList = new ArrayList<>();
                        for(int var1 = 0; var1 < size; var1++) {
                            inv.setItem(9 + (9 * var1), glass);
                            intList.add(9 + (9 * var1));
                            inv.setItem(17 + (9 * var1), glass);
                            intList.add(17 + (9 * var1));
                        }

                        int var2 = 10;
                        for(Challenge challenge : Main.getInstance().getChallengeManager().challenges) {
                            if(!intList.contains(var2)) {
                                ItemStack item2 = challenge.guiSymbol.clone();
                                meta = item2.getItemMeta();
                                String enabledSuffix = " ";
                                if(challenge.enabled) enabledSuffix = " §f[§aAktiviert§f]";
                                else enabledSuffix = " §f[§cDeaktiviert§f]";
                                meta.setDisplayName(meta.getDisplayName() + enabledSuffix);
                                item2.setItemMeta(meta);
                                inv.setItem(var2, item2);
                                var2++;
                            } else {
                                while(intList.contains(var2)) var2++;
                                ItemStack item2 = challenge.guiSymbol.clone();
                                meta = item2.getItemMeta();
                                String enabledSuffix = " ";
                                if(challenge.enabled) enabledSuffix = " §f[§aAktiviert§f]";
                                else enabledSuffix = " §f[§cDeaktiviert§f]";
                                meta.setDisplayName(meta.getDisplayName() + enabledSuffix);
                                item2.setItemMeta(meta);
                                inv.setItem(var2, item2);
                                var2++;
                            }
                        }

                        for(int var3 = inv.getSize() - 9; var3 < inv.getSize(); ++var3) {
                            inv.setItem(var3, glass);
                        }

                        ItemStack startChallenge = new ItemStack(Material.STAINED_CLAY, 1, (short) 0, DyeColor.GREEN.getDyeData());
                        meta = startChallenge.getItemMeta();
                        meta.setDisplayName("§aChallenge starten");
                        meta.addEnchant(Enchantment.FIRE_ASPECT, 1, false);
                        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        meta.setLore(Arrays.asList("", "§aStartet die Challenge und es gibt kein Zurück !"));
                        startChallenge.setItemMeta(meta);

                        inv.setItem(inv.getSize() - 5, startChallenge);

                        p.openInventory(inv);
                    } else p.sendMessage(Main.PREFIX + " §cDu bist nicht der aktuelle Hoster");
                } else if(p.getWorld().equals(Main.getInstance().getWorldManager().getWorld(WorldManager.Worlds.LOBBY))) {
                    if(lastLocations.containsKey(p.getUniqueId())) {
                        Location loc = lastLocations.get(p.getUniqueId());
                        p.teleport(loc);
                    } else {
                        p.teleport(Main.getInstance().getWorldManager().getWorld(WorldManager.Worlds.OVERWORLD).getSpawnLocation());
                    }
                }
            } else if(args.length == 1) {
                if(args[0].equals("stop")) {
                    if(Main.getInstance().isChallengeRunning()) {
                        Bukkit.broadcastMessage(Main.PREFIX + " §aChallenge wurde mit Erfolg beendet !");
                        Bukkit.broadcastMessage(Main.PREFIX + " §aModi wird in 5 Sekunden resettet !");
                        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
                            @Override
                            public void run() {
                                for(Challenge challenge : Main.getInstance().getChallengeManager().challenges) {
                                    if(challenge.enabled) {
                                        challenge.enabled = false;
                                        challenge.onChallengeDisable();
                                    }
                                }
                                for(Player p : Bukkit.getOnlinePlayers()) p.getInventory().clear();
                                PlayerListener.startedPlayers.clear();
                                Main.getInstance().getWorldManager().deleteGameWorld();
                                Bukkit.broadcastMessage(Main.PREFIX + " §aWelten wurden gelöscht und Challenges deaktiviert !");
                                Main.getInstance().setChallengeRunning(false);
                                Main.getInstance().getTimer().reset();
                                Main.getInstance().startChangeScheduler();
                                for(Player p : Bukkit.getOnlinePlayers()) {
                                    if(Main.getInstance().getForcedSpawn() != null)  {
                                        p.teleport(Main.getInstance().getForcedSpawn());
                                    } else {
                                        p.teleport(Main.getInstance().getWorldManager().getWorld(WorldManager.Worlds.LOBBY).getSpawnLocation());
                                    }
                                    p.setGameMode(GameMode.SURVIVAL);
                                }
                            }
                        }, 20 * 5);
                    } else p.sendMessage(Main.PREFIX + " §cDie Challenge läuft nicht !");
                } else {
                    if(p.getUniqueId().equals(Main.getInstance().getCurrentHoster())) {
                        if(Bukkit.getPlayer(args[0]) != null) {
                            Main.getInstance().setCurrentHoster(Bukkit.getPlayer(args[0]).getUniqueId());
                            p.sendMessage(Main.PREFIX + " §aDer Hoster wurde gewechselt");
                        } else {
                            boolean contains = false;
                            for(OfflinePlayer op : Bukkit.getOfflinePlayers()) {
                                if(op.getName().equals(args[0])) {
                                    contains = true;
                                    Main.getInstance().setCurrentHoster(op.getUniqueId());
                                    break;
                                }
                            }
                            if(contains) {
                                p.sendMessage(Main.PREFIX + " §aDer Hoster wurde gewechselt");
                            } else p.sendMessage(Main.PREFIX + " §cDieser Spieler war noch nie auf dem Server !");
                        }
                    } else p.sendMessage(Main.PREFIX + " §cDu bist nicht der Hoster !");
                }
            }
        }
        return false;
    }

}
