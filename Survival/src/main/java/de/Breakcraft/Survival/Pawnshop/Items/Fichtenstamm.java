package de.Breakcraft.Survival.Pawnshop.Items;

import de.Breakcraft.Survival.Pawnshop.PawnShopItem;
import org.bukkit.Material;

public class Fichtenstamm implements PawnShopItem {

    @Override
    public Material getMaterial() {
        return Material.SPRUCE_LOG;
    }

    @Override
    public int getWorth() {
        return 5;
    }

    @Override
    public String getName() {
        return "Fichtenstamm";
    }

}
