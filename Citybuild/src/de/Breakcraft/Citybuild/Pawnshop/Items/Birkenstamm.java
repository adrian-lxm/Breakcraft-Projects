package de.Breakcraft.Citybuild.Pawnshop.Items;

import de.Breakcraft.Citybuild.Pawnshop.PawnShopItem;
import org.bukkit.Material;

public class Birkenstamm implements PawnShopItem {

    @Override
    public Material getMaterial() {
        return Material.BIRCH_LOG;
    }

    @Override
    public int getWorth() {
        return 5;
    }

    @Override
    public String getName() {
        return "Birkenstamm";
    }

}
