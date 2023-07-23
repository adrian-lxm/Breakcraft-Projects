package de.Breakcraft.Citybuild.Pawnshop.Items;

import de.Breakcraft.Citybuild.Pawnshop.PawnShopItem;
import org.bukkit.Material;

public class Sand implements PawnShopItem {

    @Override
    public Material getMaterial() {
        return Material.SAND;
    }

    @Override
    public int getWorth() {
        return 1;
    }

    @Override
    public String getName() {
        return "Sand";
    }

}
