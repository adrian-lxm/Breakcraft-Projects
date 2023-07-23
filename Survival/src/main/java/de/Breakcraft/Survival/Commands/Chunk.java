package de.Breakcraft.Survival.Commands;

import de.Breakcraft.Survival.Main;
import de.Breakcraft.Survival.Utils.ChunkClaim;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Chunk implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length == 1) {
                if(args[0].equals("help")) {
                    p.sendMessage("§a-------------------------------------------");
                    p.sendMessage("");
                    p.sendMessage("§aAlle Command Möglichkeiten für §e/chunk");
                    p.sendMessage("");
                    p.sendMessage("§e/chunk info §a- Gibt dir Infos zum aktuellen Chunk");
                    p.sendMessage("§e/chunk claim §a- Claimt den Chunk zu deinem aktuellen Preis");
                    p.sendMessage("§e/chunk price §a- Zeigt dir deinen aktuellen Claimpreis an");
                    p.sendMessage("");
                    p.sendMessage("§a-------------------------------------------");
                } else if(args[0].equals("info")) {
                    if(Main.ccm.isChunkClaimed(p.getLocation().getChunk())) {
                        ChunkClaim claim = Main.ccm.getClaimByChunk(p.getLocation().getChunk());
                        OfflinePlayer owner = Bukkit.getOfflinePlayer(claim.owner);
                        p.sendMessage("§a-------------------------------------------");
                        p.sendMessage("");
                        p.sendMessage("§aClaim Infos zum aktuellen Chunk");
                        p.sendMessage("");
                        p.sendMessage("§aOwner: §e" + owner.getName());
                        if(claim.trusted.size() > 0) {
                            p.sendMessage("§aTrusted Players:");
                            for(UUID id : claim.trusted) {
                                OfflinePlayer trusted = Bukkit.getOfflinePlayer(id);
                                p.sendMessage("   §a- §e" + trusted.getName());
                            }
                        } else p.sendMessage("§aTrusted Players: §eNone");
                        p.sendMessage("");
                        p.sendMessage("§a-------------------------------------------");
                    } else p.sendMessage("§cDieser Chunk ist nicht geclaimed !");
                } else if(args[0].equals("claim")) {
                    if(Main.ccm.claimCount.get(p) > 0) {
                        int count = Main.ccm.claimCount.get(p) - 1;
                        int price = 1000;
                        if(count != 0) price = (int) (1000 * (3.5 * count));
                        else price = 1000;
                        if(Main.getInstance().econ.getBalance(p) >= price) {
                            if(!(Main.ccm.isChunkClaimed(p.getLocation().getChunk()))) {
                                Main.getInstance().econ.withdrawPlayer(p, price);
                                Main.ccm.createClaim(p, p.getLocation().getChunk());
                                Main.ccm.claimCount.replace(p, Main.ccm.claimCount.get(p) + 1);
                                p.sendMessage("§aDu hast den Chunk für §e" + price + "€ §ageclaimed !");
                            } else p.sendMessage("§cDieser Chunk ist schon geclaimed !");
                        } else  p.sendMessage("§cDu hast nicht genug Geld !");
                    } else {
                        if(!(Main.ccm.isChunkClaimed(p.getLocation().getChunk()))) {
                            Main.ccm.createClaim(p, p.getLocation().getChunk());
                            Main.ccm.claimCount.replace(p, Main.ccm.claimCount.get(p) + 1);
                            p.sendMessage("§aDu hast deinen ersten Chunk geclaimed !");
                        } else p.sendMessage("§cDieser Chunk ist schon geclaimed !");
                    }
                } else if(args[0].equals("price")) {
                    if(Main.ccm.claimCount.get(p) > 0) {
                        int count = Main.ccm.claimCount.get(p) - 1;
                        int price;
                        if(count != 0) price = (int) (1000 * (3.5 * count));
                        else price = 1000;
                        p.sendMessage("§aFür deinen nächste §eClaim §amusst du §e" + price + "€ §a!");
                    } else p.sendMessage("§aDein erster Claim ist gratis !");
                } else p.chat("/chunk help");
            } else if(args.length == 3) {
                if(args[0].equals("trust")) {
                    if(args[1].equals("add")) {
                        if(Main.ccm.isChunkClaimed(p.getLocation().getChunk())) {
                            ChunkClaim claim = Main.ccm.getClaimByChunk(p.getLocation().getChunk());
                            if(claim.owner.equals(p.getUniqueId())) {
                                OfflinePlayer p2 = Bukkit.getOfflinePlayer(args[2]);
                                if(p2.hasPlayedBefore()) {
                                    if(!(claim.isTrusted(p2.getUniqueId()))) {
                                        claim.addTrusted(p2.getUniqueId());
                                        p.sendMessage("§e" + p2.getName() + " §awurde getrusted !");
                                    } else p.sendMessage("§cDieser Spieler ist schon getrusted !");
                                } else p.sendMessage("§e" + args[2] + " §chat noch nie auf dem Server gespielt !");
                            } else p.sendMessage("§cDieser Chunk gehört dir nicht !");
                        } else p.sendMessage("§cDieser Chunk ist nicht geclaimed !");
                    } else if(args[1].equals("remove")) {
                        if(Main.ccm.isChunkClaimed(p.getLocation().getChunk())) {
                            ChunkClaim claim = Main.ccm.getClaimByChunk(p.getLocation().getChunk());
                            if(claim.owner.equals(p.getUniqueId())) {
                                OfflinePlayer p2 = Bukkit.getOfflinePlayer(args[2]);
                                if(p2.hasPlayedBefore()) {
                                    if(claim.isTrusted(p2.getUniqueId())) {
                                        claim.removeTrusted(p2.getUniqueId());
                                        p.sendMessage("§e" + p2.getName() + " §awurde enttrusted !");
                                    } else p.sendMessage("§cDieser Spieler ist schon getrusted !");
                                } else p.sendMessage("§e" + args[2] + " §chat noch nie auf dem Server gespielt !");
                            } else p.sendMessage("§cDieser Chunk gehört dir nicht !");
                        } else p.sendMessage("§cDieser Chunk ist nicht geclaimed !");
                    } else p.chat("/chunk help");
                } else p.chat("/chunk help");
            } else p.chat("/chunk help");
        } else sender.sendMessage("Das kannst du nur als Spieler");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> firstChoices = Arrays.asList("help", "info", "claim", "price");
        if(args.length > 0) {
            List<String> complete = new ArrayList<>();
            for(String i : firstChoices) {
                if(i.startsWith(args[0])) complete.add(i);
            }
            return complete;
        } else return firstChoices;
    }

}
