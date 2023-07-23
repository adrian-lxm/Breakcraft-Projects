package de.Breakcraft.Survival.Listeners;

import de.Breakcraft.Survival.Main;
import de.Breakcraft.Survival.Utils.ChunkClaim;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class ChunkClaimListeners implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(e.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            if(Main.ccm.isChunkClaimed(block.getChunk())) {
                ChunkClaim claim = Main.ccm.getClaimByChunk(block.getChunk());
                if(!(claim.isTrusted(e.getPlayer()))) {
                    if(!(claim.owner.equals(e.getPlayer().getUniqueId()))) {
                        if(!(e.getPlayer().hasPermission("breakcraft.chunks.others"))) e.setCancelled(true);
                    }
                }
            }
        } else if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            if(Main.ccm.isChunkClaimed(block.getChunk())) {
                ChunkClaim claim = Main.ccm.getClaimByChunk(block.getChunk());
                if(!(claim.isTrusted(e.getPlayer()))) {
                    if(!(claim.owner.equals(e.getPlayer().getUniqueId()))) {
                        if(!(e.getPlayer().hasPermission("breakcraft.chunks.others"))) e.setCancelled(true);
                    }
                }
            }
        } else if(e.getAction() == Action.PHYSICAL) {
            if(Main.ccm.isChunkClaimed(e.getPlayer().getLocation().getChunk())) {
                ChunkClaim claim = Main.ccm.getClaimByChunk(e.getPlayer().getLocation().getChunk());
                if(!(claim.isTrusted(e.getPlayer()))) {
                    if(!(claim.owner.equals(e.getPlayer().getUniqueId()))) {
                        if(!(e.getPlayer().hasPermission("breakcraft.chunks.others"))) e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player damager = (Player) e.getDamager();
            Player p = (Player) e.getEntity();
            if(Main.ccm.isChunkClaimed(p.getLocation().getChunk())) {
                ChunkClaim claim = Main.ccm.getClaimByChunk(p.getLocation().getChunk());
                if(!(claim.isTrusted(damager) || claim.owner.equals(damager.getUniqueId()))) {
                    if(!(damager.hasPermission("breakcraft.chunks.others"))) {
                        if(claim.isTrusted(p)) e.setCancelled(true);
                        if(claim.owner.equals(p.getUniqueId())) e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(EntityExplodeEvent e) {
        List<Block> blocks = e.blockList();
        for(Block block: blocks) {
            if(Main.ccm.isChunkClaimed(block.getChunk())) {
                e.setCancelled(true);
            }
        }
    }

}
