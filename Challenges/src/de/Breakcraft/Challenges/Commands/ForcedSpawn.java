package de.Breakcraft.Challenges.Commands;

import de.Breakcraft.Challenges.Main;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForcedSpawn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(p.hasPermission("Breakcraft.forcedSpawn")) {
                Main.getInstance().getConfig().set("forced-spawn", p.getLocation());
                Main.getInstance().saveConfig();
                p.sendMessage("[§aBreackcraft§f] §aForced Spawn wurde gesetzt !");
                p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
            } else p.sendMessage("[§aBreackcraft§f] §cDazu hast du nicht die Rechte !");
        }
        return false;
    }

}
