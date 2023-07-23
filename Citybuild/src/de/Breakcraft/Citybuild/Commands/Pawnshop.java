package de.Breakcraft.Citybuild.Commands;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Pawnshop implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            p.openInventory(de.Breakcraft.Citybuild.Pawnshop.Pawnshop.createShopInv());
            p.playSound(p.getLocation(), Sound.ORB_PICKUP, 1, 1);
        }
        return false;
    }

}
