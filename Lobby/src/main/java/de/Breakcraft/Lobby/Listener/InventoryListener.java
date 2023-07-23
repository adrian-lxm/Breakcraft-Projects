package de.Breakcraft.Lobby.Listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.Breakcraft.Lobby.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getWhoClicked() instanceof Player) {
            if(e.getCurrentItem() != null) {
                if(e.getCurrentItem().isSimilar(Main.getCompassItem())) e.setCancelled(true);
            }
            if(e.getClickedInventory().getType() != InventoryType.CREATIVE) {
                if(e.getClickedInventory().getTitle() != null) {
                    if(e.getClickedInventory().getTitle().equals("§5Serverauswahl : Breakcraft")) {
                        e.setCancelled(true);
                        if(e.getCurrentItem() != null) {
                            Player p = (Player) e.getWhoClicked();
                            switch(e.getCurrentItem().getType()) {
                                case GRASS:
                                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                                    out.writeUTF("Connect");
                                    out.writeUTF("Survival");
                                    p.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
                                    break;
                                case DIAMOND_SWORD:
                                    p.sendMessage(ChatColor.RED + "Dieser Spielmodus ist derzeit nicht verfügbar !");
                                    p.getOpenInventory().close();
                                    /**
                                    ByteArrayDataOutput out2 = ByteStreams.newDataOutput();
                                    out2.writeUTF("Connect");
                                    out2.writeUTF("Challenges");
                                    p.sendPluginMessage(Main.getInstance(), "BungeeCord", out2.toByteArray());
                                     **/
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        if(e.getItemDrop().getItemStack().isSimilar(Main.getCompassItem())) e.setCancelled(true);
    }

}
