package de.Breakcraft.Challenges.Challenges;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class NoneArmor extends Challenge {
    public static NoneArmor instance;

    public NoneArmor() {
        instance = this;

        name = "None Armor";
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§a" + name);
        List<String> lore = Arrays.asList("", "§aWofür braucht man Rüstung ?");
        meta.setLore(lore);
        item.setItemMeta(meta);
        guiSymbol = item;
        listener = new Listeners();
    }

    @Override
    public void onChallengeEnable() {
        enableChallengeSubSystem();
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§cGESPERRTER SLOT");
        meta.setLore(Arrays.asList("", "§cNone Armor mein Freundchen ;)"));
        meta.addEnchant(Enchantment.ARROW_FIRE, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.getInventory().setHelmet(item);
            p.getInventory().setChestplate(item);
            p.getInventory().setLeggings(item);
            p.getInventory().setBoots(item);
        }
    }

    @Override
    public void onChallengeDisable() {
        disableChallengeSubSystem();
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.getInventory().setHelmet(null);
            p.getInventory().setChestplate(null);
            p.getInventory().setLeggings(null);
            p.getInventory().setBoots(null);
        }
    }

    @Override
    public void createSchedulers() {

    }

    private class Listeners implements Listener {

        @EventHandler
        public void onInventoryClick(InventoryClickEvent e) {
            if(e.getWhoClicked() instanceof Player) {
                Player p = (Player) e.getWhoClicked();
                if(e.getCurrentItem() != null) {
                    if(e.getCurrentItem().getType() == Material.BARRIER) e.setCancelled(true);
                }
            }
        }

    }

}
