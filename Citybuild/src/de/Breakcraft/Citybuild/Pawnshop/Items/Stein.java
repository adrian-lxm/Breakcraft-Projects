package de.Breakcraft.Citybuild.Pawnshop.Items;

import de.Breakcraft.Citybuild.Pawnshop.PawnShopItem;
import org.bukkit.Material;

public class Stein implements PawnShopItem {

    @Override
    public Material getMaterial() {
        return Material.STONE;
    }

    @Override
    public int getWorth() {
        return 2;
    }

    @Override
    public String getName() {
        return "Stein";
    }

}
