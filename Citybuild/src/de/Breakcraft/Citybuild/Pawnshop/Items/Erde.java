package de.Breakcraft.Citybuild.Pawnshop.Items;

import de.Breakcraft.Citybuild.Pawnshop.PawnShopItem;
import org.bukkit.Material;

public class Erde implements PawnShopItem {

    @Override
    public Material getMaterial() {
        return Material.DIRT;
    }

    @Override
    public int getWorth() {
        return 1;
    }

    @Override
    public String getName() {
        return "Erde";
    }

}
