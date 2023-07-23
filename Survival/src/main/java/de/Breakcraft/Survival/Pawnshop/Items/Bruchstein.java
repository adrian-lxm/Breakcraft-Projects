package de.Breakcraft.Survival.Pawnshop.Items;

import de.Breakcraft.Survival.Pawnshop.PawnShopItem;
import org.bukkit.Material;

public class Bruchstein implements PawnShopItem {

    @Override
    public Material getMaterial() {
        return Material.COBBLESTONE;
    }

    @Override
    public int getWorth() {
        return 2;
    }

    @Override
    public String getName() {
        return "Bruchstein";
    }

}
