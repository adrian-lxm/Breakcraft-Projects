package de.Breakcraft.Lobby.Commands;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Servernpc implements CommandExecutor {
    public static HashMap<Entity, String> hitNPCs = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(p.hasPermission("breakcraft.spawnNPC")) {
                if(args.length == 1) {
                    switch(args[0]) {
                        case "Survival":
                            Entity entity = p.getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE);
                            noAI(entity);
                            entity.setCustomName("§aSurvival §f[§eRechtsklick§f]");
                            entity.setCustomNameVisible(true);
                            hitNPCs.put(entity, "Survival");
                            p.sendMessage("§aEntity erstellt !");
                            break;

                        case "clear":
                            for(Entity entity3 : hitNPCs.keySet()) {
                                ((LivingEntity) entity3).setHealth(0);
                            }
                            hitNPCs.clear();
                            break;
                        default:
                            break;
                    }
                } else p.sendMessage("§cGebe den Server an !");
            } else p.sendMessage("§cDu hast nicht die Rechte dazu !");
        }
        return false;
    }

    private void noAI(Entity bukkitEntity) {
        net.minecraft.server.v1_8_R3.Entity nmsEntity = ((CraftEntity) bukkitEntity).getHandle();
        NBTTagCompound tag = nmsEntity.getNBTTag();
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        nmsEntity.c(tag);
        tag.setInt("NoAI", 1);
        nmsEntity.f(tag);
    }

}
