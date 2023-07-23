package de.Breakcraft.Lobby;

import com.mysql.cj.jdbc.MysqlDataSource;
import de.Breakcraft.Lobby.Commands.ForcedSpawn;
import de.Breakcraft.Lobby.Commands.Servernpc;
import de.Breakcraft.Lobby.Listener.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Main extends JavaPlugin {
    public static Main instance;
    public static HashMap<Player, Integer> infos = new HashMap<>();
    public static List<Integer> taskIDs = new ArrayList<>();
    private static ItemStack compass;
    public static DataSource dataSource;
    public static File file;
    private final String sqlUsername = "";
    private final String sqlPassword = "";

    @Override
    public void onEnable() {
        instance = this;

        setUpScoreboardScheduler();

        getCommand("forcedSpawn").setExecutor(new ForcedSpawn());
        getCommand("servernpc").setExecutor(new Servernpc());

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new JoinListener(), this);
        pm.registerEvents(new HealthFoodListener(), this);
        pm.registerEvents(new InteractListener(), this);
        pm.registerEvents(new InventoryListener(), this);
        pm.registerEvents(new DoubleJumpListeners(), this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUrl("jdbc:mysql://localhost:3306/Breakcraft?characterEncoding=utf8&autoReconnect=true");
        mysqlDataSource.setUser(sqlUsername);
        mysqlDataSource.setPassword(sqlPassword);
        dataSource = mysqlDataSource;

        compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.setDisplayName("§aServerauswahl");
        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        List<String> lore = Arrays.asList("", "§aDrücke Rechtsklick und sehe", "§aalle unsere Server / Gamemodis !");
        meta.setLore(lore);
        compass.setItemMeta(meta);

    }

    @Override
    public void onDisable() {
        for(Entity entity : Servernpc.hitNPCs.keySet()) {
            ((LivingEntity) entity).setHealth(0);
        }
        Servernpc.hitNPCs.clear();
    }

    public static Main getInstance() {
        return instance;
    }

    public static ItemStack getCompassItem() {
        return compass;
    }

    private void setUpScoreboardScheduler() {
        int i = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if(Bukkit.getOnlinePlayers().size() > 0) {
                    int count = 0;
                    try(Connection connection = Main.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from ServerInfo WHERE id = 1")) {
                        ResultSet set = preparedStatement.executeQuery();
                        set.next();
                        count = Integer.parseInt(set.getString(2));
                        set.close();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        boolean updated = false;
                        Scoreboard board = p.getScoreboard();
                        Objective objective = (Objective) board.getObjectives().toArray()[0];
                        Score players = objective.getScore("   §e" + count + " §b/ §e1000");
                        if(!players.isScoreSet()) {
                            board.resetScores("   §e" + Main.infos.get(p) + " §b/ §e1000");
                            players.setScore(5);
                            Main.infos.replace(p, count);
                            updated = true;
                        }

                        if(updated) {
                            p.setScoreboard(board);
                        }
                    }
                }
            }
        }, 0, 20);
        taskIDs.add(i);
    }

}
