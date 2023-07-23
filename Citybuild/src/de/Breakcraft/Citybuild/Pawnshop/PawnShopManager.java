package de.Breakcraft.Citybuild.Pawnshop;

import de.Breakcraft.Citybuild.Pawnshop.Items.*;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class PawnShopManager {
    public List<PawnShopItem> items = new ArrayList<>();

    public PawnShopManager() {
        items.add(new Erde());
        items.add(new Eichenstamm());
        items.add(new Birkenstamm());
        items.add(new Schwarzeichenstamm());
        items.add(new Fichtenstamm());
        items.add(new Stein());
        items.add(new Bruchstein());
        items.add(new Sand());
        items.add(new Kies());
        items.add(new Kohle());
        items.add(new Eisenbarren());
        items.add(new Netherrack());
        items.add(new Quarz());
    }

    public PawnShopItem getPawnShopItemByMaterial(Material material) {
        for(PawnShopItem item : items) {
            if(item.getMaterial() == material) return item;
        }
        return null;
    }

}
