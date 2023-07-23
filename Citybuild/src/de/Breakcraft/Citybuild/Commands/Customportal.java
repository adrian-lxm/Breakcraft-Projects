package de.Breakcraft.Citybuild.Commands;

import de.Breakcraft.Citybuild.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Customportal  implements CommandExecutor {
    public static HashMap<Player, String> creatingPortals = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(p.hasPermission("breakcraft.customportals.create")) {
                if(!creatingPortals.containsKey(p)) {
                    if(args.length == 1) {
                        if(Bukkit.getWorld(args[0]) != null) {
                            creatingPortals.put(p, args[0]);

                        } else p.sendMessage(Main.PREFIX + " §cDiese Welt existiert nicht !");
                    } else p.sendMessage(Main.PREFIX + " §cZu wenige oder zu viele Argumente");
                } else p.sendMessage(Main.PREFIX + " §c");
            } else p.sendMessage(Main.PREFIX + " §cDazu hast du nicht die Rechte !");
        }
        return false;
    }

}
