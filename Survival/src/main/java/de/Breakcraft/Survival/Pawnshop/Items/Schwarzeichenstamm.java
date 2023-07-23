package de.Breakcraft.Survival.Pawnshop.Items;

import de.Breakcraft.Survival.Pawnshop.PawnShopItem;
import org.bukkit.Material;

public class Schwarzeichenstamm implements PawnShopItem {

    @Override
    public Material getMaterial() {
        return Material.DARK_OAK_LOG;
    }

    @Override
    public int getWorth() {
        return 5;
    }

    @Override
    public String getName() {
        return "Schwarzeichenstamm";
    }

}
