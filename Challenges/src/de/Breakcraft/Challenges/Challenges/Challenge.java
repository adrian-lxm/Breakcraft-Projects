package de.Breakcraft.Challenges.Challenges;

import de.Breakcraft.Challenges.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public abstract class Challenge {
    public String name;
    public int[] schedulers = null;
    public ItemStack guiSymbol;
    public boolean enabled = false;
    public Listener listener;

    public abstract void onChallengeEnable();
    public abstract void onChallengeDisable();
    public abstract void createSchedulers();

    public void enableChallengeSubSystem() {
        if(listener != null) {
            Bukkit.getPluginManager().registerEvents(listener, Main.getInstance());
        }
        createSchedulers();
    }

    public void disableChallengeSubSystem() {
        if(listener != null) {
            HandlerList.unregisterAll(listener);
        }
        if(schedulers != null) {
            for(int i : schedulers) {
                Bukkit.getScheduler().cancelTask(i);
            }
        }
    }

}
