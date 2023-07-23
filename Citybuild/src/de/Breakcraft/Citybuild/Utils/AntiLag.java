package de.Breakcraft.Citybuild.Utils;

import de.Breakcraft.Citybuild.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AntiLag implements Listener {
    private int taskID;
    private int counter = 10;
    private HashMap<Item, Integer> beforeListAdd = new HashMap<>();
    private List<Item> itemsOnGround = new ArrayList<>();

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent e) {
        int i = Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                beforeListAdd.remove(e.getEntity());
                itemsOnGround.add(e.getEntity());
            }
        }, 20 * 60);
        beforeListAdd.put(e.getEntity(), i);
    }

    @EventHandler
    public void onDespawn(ItemDespawnEvent e) {
        if(beforeListAdd.containsKey(e.getEntity())) {
            Bukkit.getScheduler().cancelTask(beforeListAdd.get(e.getEntity()));
            beforeListAdd.remove(e.getEntity());
        } else if(itemsOnGround.contains(e.getEntity())) {
            itemsOnGround.remove(e.getEntity());
        }
    }

    @EventHandler
    public void onItemTake(InventoryPickupItemEvent e) {
        if(beforeListAdd.containsKey(e.getItem())) {
            Bukkit.getScheduler().cancelTask(beforeListAdd.get(e.getItem()));
            beforeListAdd.remove(e.getItem());
        } else if(itemsOnGround.contains(e.getItem())) {
            itemsOnGround.remove(e.getItem());
        }
    }

    public void init() {
        Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
        taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {
            @Override
            public void run() {
                counter--;
                if (counter != 0) Bukkit.broadcastMessage("[§5Breakcraft§f] §aDie Items auf dem Boden werden in §e" + counter + " Minuten §aentfernt !");
                else {
                    if(!itemsOnGround.isEmpty()) {
                        for(Item item : itemsOnGround) {
                            item.remove();
                        }
                        itemsOnGround.clear();
                    }
                    Bukkit.broadcastMessage("[§5Breakcraft§f] §aAlle Items gecleared! Nächster Clear in 10min...");
                    counter = 10;
                }
            }
        }, 20 * 60, 20 * 60);
    }

}
