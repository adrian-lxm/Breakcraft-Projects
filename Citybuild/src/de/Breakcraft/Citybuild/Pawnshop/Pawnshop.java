package de.Breakcraft.Citybuild.Pawnshop;

import de.Breakcraft.Citybuild.Main;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.util.Arrays;
import java.util.List;

public class Pawnshop {
    public static PawnShopManager manager = new PawnShopManager();

    public static Inventory createShopInv() {
        Inventory inv = Bukkit.createInventory(null, 6*9, "§f§5Pfandhaus");
        ItemStack glass = new ItemStack(Material.STAINED_GLASS_PANE);
        glass.setData(new MaterialData(Material.STAINED_GLASS_PANE, DyeColor.GRAY.getDyeData()));
        ItemMeta meta = glass.getItemMeta();
        meta.setDisplayName(" ");
        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        glass.setItemMeta(meta);
        ItemStack pawnshop = new ItemStack(Material.WORKBENCH);
        meta = pawnshop.getItemMeta();
        meta.setDisplayName("§aPfandhaus");
        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> lore1 = Arrays.asList("", "§aVerkaufe deine Sachen hier gegen Ingame €");
        meta.setLore(lore1);
        pawnshop.setItemMeta(meta);
        for(int i = 0; i < 9; i++) {
            inv.setItem(i, glass);
        }

        inv.setItem(4, pawnshop);

        inv.setItem(9, glass);
        inv.setItem(17, glass);

        inv.setItem(18, glass);
        inv.setItem(26, glass);

        inv.setItem(27, glass);
        inv.setItem(35, glass);

        inv.setItem(36, glass);
        inv.setItem(44, glass);

        for(int i = 45; i < inv.getSize(); i++) {
            inv.setItem(i, glass);
        }

        List<String> lore2 = Arrays.asList("", "§aLinksklick um ein Stück zu verkaufen", "§eRechstklick um nächsten Slot zu verkaufen");

        int i = 10;
        int i2 = 17;
        for(PawnShopItem item : manager.items) {
            ItemStack itemstack = new ItemStack(item.getMaterial());
            meta = itemstack.getItemMeta();
            meta.setDisplayName("§a" + item.getName() + "  -  " + item.getWorth() + "€");
            meta.setLore(lore2);
            itemstack.setItemMeta(meta);
            inv.setItem(i, itemstack);
            i++;
            if(i == i2) {
                i += 2;
                i2 += 9;
            }
        }

        return inv;
    }

    public static void handleEvent(Player p, boolean slot, ItemStack item) {
        if(slot) {
            PawnShopItem item1 = manager.getPawnShopItemByMaterial(item.getType());
            if(p.getInventory().contains(item1.getMaterial())) {
                int slot2 = 0;
                for(int i = 0; i < p.getInventory().getSize(); i++) {
                    if(p.getInventory().getItem(i) != null) {
                        if(p.getInventory().getItem(i).getType() == item1.getMaterial()) {
                            slot2 = i;
                            break;
                        }
                    }
                }
                int count = p.getInventory().getItem(slot2).getAmount();
                p.getInventory().clear(slot2);
                Main.getInstance().econ.depositPlayer(p, count*item1.getWorth());
                p.sendMessage("§aDu hast §e" + (count*item1.getWorth()) + " §a€ für §e" + count + "§ax " + item1.getName() + " gekriegt !");
            } else p.sendMessage("§cDu hast keine " + item1.getName() + " in deinem Inventar !");
        } else {
            PawnShopItem item1 = manager.getPawnShopItemByMaterial(item.getType());
            if(p.getInventory().contains(item1.getMaterial())) {
                int slot2 = 0;
                for(int i = 0; i < p.getInventory().getSize(); i++) {
                    if(p.getInventory().getItem(i) != null) {
                        if(p.getInventory().getItem(i).getType() == item1.getMaterial()) {
                            slot2 = i;
                            break;
                        }
                    }
                }
                if(p.getInventory().getItem(slot2).getAmount() == 1) {
                    p.getInventory().clear(slot2);
                    Main.getInstance().econ.depositPlayer(p, item1.getWorth());
                    p.sendMessage("§aDu hast §e" + item1.getWorth() + " §a€ für §e1§ax " + item1.getName() + " gekriegt !");
                } else {
                    int newAmount = p.getInventory().getItem(slot2).getAmount() - 1;
                    p.getInventory().getItem(slot2).setAmount(newAmount);
                    Main.getInstance().econ.depositPlayer(p, item1.getWorth());
                    p.sendMessage("§aDu hast §e" + item1.getWorth() + " §a€ für §e1§ax " + item1.getName() + " gekriegt !");
                }
            } else p.sendMessage("§cDu hast keine " + item1.getName() + " in deinem Inventar !");
        }
    }

}
