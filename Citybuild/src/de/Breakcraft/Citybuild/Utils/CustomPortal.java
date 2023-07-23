package de.Breakcraft.Citybuild.Utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class CustomPortal {
    public List<Location> portalLocs = new ArrayList<>();
    public Location direction;

    public CustomPortal(List<Block> blocks, Location direction) {
        checkForPortals(blocks);
        this.direction = direction;
    }

    private void checkForPortals(List<Block> blocks) {
        for(Block block : blocks) {
            if(block.getType() == Material.PORTAL || block.getType() == Material.ENDER_PORTAL) {
                portalLocs.add(block.getLocation());
            }
        }
    }

}