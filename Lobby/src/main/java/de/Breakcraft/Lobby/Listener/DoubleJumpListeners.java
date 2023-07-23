package de.Breakcraft.Lobby.Listener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import java.util.HashMap;

public class DoubleJumpListeners implements Listener {
    public static HashMap<Player, Boolean> doubleJump = new HashMap<>();

    @EventHandler
    public void onToggleFly(PlayerToggleFlightEvent e) {
        if(e.getPlayer().hasPermission("breakcraft.doublejump")) {
            if (e.getPlayer().getGameMode() == GameMode.SURVIVAL || e.getPlayer().getGameMode() == GameMode.ADVENTURE) {
                e.setCancelled(true);
                if (doubleJump.get(e.getPlayer())) return;
                doubleJump.replace(e.getPlayer(), true);
                e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().setY(1));
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if(e.getPlayer().hasPermission("breakcraft.doublejump")) {
            if(e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                if(doubleJump.get(e.getPlayer()) && e.getPlayer().isOnGround()) {
                    doubleJump.replace(e.getPlayer(), false);
                }
            }
        }
    }

    @EventHandler
    public void onGamemodeChange(PlayerGameModeChangeEvent e) {
        if(e.getPlayer().hasPermission("breakcraft.doublejump")) {
            if (e.getNewGameMode() == GameMode.SURVIVAL || e.getNewGameMode() == GameMode.ADVENTURE)
                e.getPlayer().setAllowFlight(true);
        }
    }

}
