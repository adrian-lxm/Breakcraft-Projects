package de.Breakcraft.Challenges.Challenges;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class LowLive extends Challenge {
    public static LowLive instance;

    public LowLive() {
        instance = this;
        name = "Low Live";
        ItemStack item = new ItemStack(Material.RED_ROSE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§a" + name);
        List<String> lore = Arrays.asList("", "§aPass besser auf, denn du stirbst schneller !");
        meta.setLore(lore);
        item.setItemMeta(meta);
        guiSymbol = item;
    }

    @Override
    public void onChallengeEnable() {
        enableChallengeSubSystem();
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.setMaxHealth(2 * 3);
        }
    }

    @Override
    public void onChallengeDisable() {
        disableChallengeSubSystem();
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.resetMaxHealth();
            p.setHealth(20);
        }
    }

    @Override
    public void createSchedulers() {}

}
