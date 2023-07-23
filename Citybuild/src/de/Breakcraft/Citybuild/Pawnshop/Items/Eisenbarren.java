package de.Breakcraft.Citybuild.Pawnshop.Items;

import de.Breakcraft.Citybuild.Pawnshop.PawnShopItem;
import org.bukkit.Material;

public class Eisenbarren implements PawnShopItem {

    @Override
    public Material getMaterial() {
        return Material.IRON_INGOT;
    }

    @Override
    public int getWorth() {
        return 90;
    }

    @Override
    public String getName() {
        return "Eisenbarren";
    }

}
