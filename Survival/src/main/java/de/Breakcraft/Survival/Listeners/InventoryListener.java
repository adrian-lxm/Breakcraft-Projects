package de.Breakcraft.Survival.Listeners;

import de.Breakcraft.Survival.Pawnshop.Pawnshop;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getWhoClicked() instanceof Player) {
            Player p = (Player) e.getWhoClicked();
            if(e.getView().getTitle().equals("§f§5Pfandhaus")) {
                e.setCancelled(true);
                if(e.getCurrentItem() != null) {
                    if(e.getCurrentItem().getType() != Material.GRAY_STAINED_GLASS_PANE && e.getCurrentItem().getType() != Material.CRAFTING_TABLE) {
                        Pawnshop.handleEvent(p, e.isRightClick(), e.getCurrentItem());
                    }
                }
            }
        }
    }


}
