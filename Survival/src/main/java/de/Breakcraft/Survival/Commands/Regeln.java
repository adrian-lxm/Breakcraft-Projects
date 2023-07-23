package de.Breakcraft.Survival.Commands;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class Regeln implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player) {
            Player p = (Player) commandSender;
            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta meta = (BookMeta) book.getItemMeta();
            meta.setAuthor("§aBreakcraft Team");
            meta.setTitle("§5Breakcraft Survival Guide");
            meta.addPage("§aEinführung\n\n§aHier hast du eine kleine Kurzfassung zu allen Möglichkeiten hier !");
            meta.addPage("§aRegeln\n\n§a1. Respekt gegen über jedem\n§a2.Keine Hacks\n§a3. Kein Griefing (dazu zählt auch andere Töten)\n§a4. ChunkClaims nicht dafür nutzen um Flächen die unbebaut sind für sich zu behalten.\n§a5. Keine Beledigungen oder Diskriminierung");
            meta.addPage("§Einige nützliche Commands\n\n§a1. /pawnshop\nVerkaufe Items gegen Ingame Geld\n\n§a2. /chunk\nClaime Chunks und sichere dein Gebiet");
            book.setItemMeta(meta);
            p.getInventory().addItem(book);
            p.sendMessage("§aDu hast ein Buch mit den Regeln gekriegt !");
        }
        return false;
    }

}
