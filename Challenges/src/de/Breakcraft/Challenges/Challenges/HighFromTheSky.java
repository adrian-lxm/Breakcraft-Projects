package de.Breakcraft.Challenges.Challenges;

import de.Breakcraft.Challenges.Main;
import de.Breakcraft.Challenges.Utils.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.List;

public class HighFromTheSky extends Challenge {

    public HighFromTheSky() {
        name = "High from the Sky";
        ItemStack item = new ItemStack(Material.ANVIL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§a" + name);
        List<String> lore = Arrays.asList("", "§aDeck deinen Kopf, denn es regnet Ambosse vom Himmel !");
        meta.setLore(lore);
        item.setItemMeta(meta);
        guiSymbol = item;
    }

    @Override
    public void onChallengeEnable() {
        enableChallengeSubSystem();
    }

    @Override
    public void onChallengeDisable() {
        disableChallengeSubSystem();
    }

    @Override
    public void createSchedulers() {
        int i = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                if (!enabled) {return;}
                for(Player p : Bukkit.getOnlinePlayers()) {
                    if(!(p.getWorld().equals(Main.getInstance().getWorldManager().getWorld(WorldManager.Worlds.LOBBY)))) {
                        Location loc = null;
                        for(int i = 10; i != 3; i--) {
                            Location loc2 = new Location(p.getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY() + i, p.getLocation().getBlockZ());
                            if(p.getWorld().getBlockAt(loc2).getType() == Material.AIR) {
                                loc = loc2;
                                break;
                            } else continue;
                        }
                        if(loc != null) {
                            FallingBlock block = p.getLocation().getWorld().spawnFallingBlock(loc, new MaterialData(Material.ANVIL));
                            block.setDropItem(false);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
                                @Override
                                public void run() {
                                    block.getLocation().getWorld().getBlockAt(block.getLocation()).setType(Material.AIR);
                                }
                            }, 5 * 20);
                        }
                    }
                }
            }
        }, 0, 10);
        schedulers = new int[] { i };
    }

}
