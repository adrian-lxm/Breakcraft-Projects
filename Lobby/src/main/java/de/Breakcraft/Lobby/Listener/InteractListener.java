package de.Breakcraft.Lobby.Listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.Breakcraft.Lobby.Commands.Servernpc;
import de.Breakcraft.Lobby.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class InteractListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(!(e.getPlayer().hasPermission("Breakcraft.Lobby.Interact"))) e.setCancelled(true);
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(e.getItem() != null) {
                if(e.getItem().isSimilar(Main.getCompassItem())) {
                    Inventory inv = Bukkit.createInventory(null, 5*9, "§5Serverauswahl : Breakcraft");
                    ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.GRAY.getDyeData());
                    ItemMeta meta = glass.getItemMeta();
                    meta.setDisplayName(" ");
                    meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    glass.setItemMeta(meta);
                    ItemStack grass = new ItemStack(Material.GRASS);
                    meta = grass.getItemMeta();
                    meta.setDisplayName("§aSurvival");
                    List<String> lore = Arrays.asList("", "§aSpiele mit deinen Freunden in einer ", "§anormalen Minecraft Welt", "", "§aAktuelle Minecraft-Version: §e1.17.1", "§cBitte spiele, wenn möglich, mit mind. 1.13 !");
                    meta.setLore(lore);
                    grass.setItemMeta(meta);

                    ItemStack sapling = new ItemStack(Material.DIAMOND_SWORD);
                    meta = sapling.getItemMeta();
                    meta.setDisplayName("§aChallenges");
                    meta.addEnchant(Enchantment.FIRE_ASPECT, 1, true);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    lore = Arrays.asList("", "§aSpiele Minecraft mit deutlichen Verschwerungen durch !", ChatColor.RED + "Derzeit nicht verfügbar !");
                    meta.setLore(lore);
                    sapling.setItemMeta(meta);

                    for(int i = 0; i < 9; i++) {
                        inv.setItem(i, glass);
                    }

                    inv.setItem(9, glass);
                    inv.setItem(17, glass);
                    inv.setItem(18, glass);
                    inv.setItem(26, glass);
                    inv.setItem(27, glass);
                    inv.setItem(35, glass);

                    inv.setItem(20, grass);
                    inv.setItem(24, sapling);

                    for(int i = 36; i < inv.getSize(); i++) {
                        inv.setItem(i, glass);
                    }

                    e.getPlayer().openInventory(inv);
                }
            }
        }
    }

    @EventHandler
    public void entityInteract(PlayerInteractAtEntityEvent e) {
        if(e.getRightClicked() instanceof Player) return;
        if(Servernpc.hitNPCs.containsKey(e.getRightClicked())) {
            ByteArrayDataOutput out2 = ByteStreams.newDataOutput();
            out2.writeUTF("Connect");
            out2.writeUTF(Servernpc.hitNPCs.get(e.getRightClicked()));
            e.getPlayer().sendPluginMessage(Main.getInstance(), "BungeeCord", out2.toByteArray());
        }
    }

}
