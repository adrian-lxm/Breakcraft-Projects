package de.Breakcraft.Citybuild.Utils;

import de.Breakcraft.Citybuild.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Random;

public class WorldManager {
    public boolean generatingWorlds = true;
    private int resetterID;
    private long seed;
    private Random random = new Random();

    public void init() {
        randomizeSeed();
        for(Worlds world : Worlds.values()) {
            if(Bukkit.getWorld(world.name) == null) {
                generateWorld(world);
            }
        }
        generatingWorlds = false;
    }

    public void generateWorld(Worlds world) {
        WorldCreator creator = new WorldCreator(world.name).environment(world.type);
        World world2 = creator.createWorld();
        world2.getBlockAt(world2.getSpawnLocation().subtract(0, 1, 0)).setType(Material.BEDROCK);
        world.spawn = world2.getSpawnLocation();
    }

    public void regenerateWorlds() {
        generatingWorlds = true;
        for(Worlds world : Worlds.values()) {
            if(Bukkit.getWorld(world.name) != null) {
                for(Player p : Bukkit.getWorld(world.name).getPlayers()) {
                    World world2 = Bukkit.getWorld("citybuild");
                    p.teleport(world2.getSpawnLocation());
                    p.sendMessage(Main.PREFIX + " §cDu wurdest in die Plotwelt gebracht, weil die Farmwelten neu generiert werden !");
                }
                World world2 = Bukkit.getWorld(world.name());
                Bukkit.unloadWorld(world2, false);
                deleteFolder(world2.getWorldFolder());
                generateWorld(world);
            }
        }
    }

    private void deleteFolder(File file) {
        for(File underFile : file.listFiles()) {
            if(file.isFile()) {
                file.delete();
            } else deleteFolder(file);
        }
        file.delete();
    }

    public void randomizeSeed() {
        seed = random.nextLong();
    }

    private enum Worlds {
        OVERWORLD("farmwelt_overwold", World.Environment.NORMAL),
        NETHER("farmwelt_nether", World.Environment.NETHER),
        END("farmwelt_the_end", World.Environment.THE_END);

        private String name;
        private World.Environment type;
        public Location spawn;
        private Worlds(String name, World.Environment type) {
            this.name = name;
            this.type = type;
        }
    }

    private class WorldResetter implements Runnable {
        private long nextMonday;

        public WorldResetter() {
            LocalDateTime localDate = LocalDateTime.now();
            localDate = localDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
            nextMonday = localDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }

        @Override
        public void run() {
            if(nextMonday < System.currentTimeMillis()) {
                Bukkit.broadcastMessage(Main.PREFIX + " §aDie Farmwelten werden jetzt zurückgesetzt !");
                regenerateWorlds();
                LocalDateTime localDate = LocalDateTime.now();
                localDate = localDate.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
                nextMonday = localDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                generatingWorlds = false;
                Bukkit.broadcastMessage(Main.PREFIX + " §aFarmwelten wurden regeneriert !");
            }
        }

    }

}
