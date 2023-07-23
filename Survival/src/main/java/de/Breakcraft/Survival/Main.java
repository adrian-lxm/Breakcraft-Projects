package de.Breakcraft.Survival;

import com.mysql.cj.jdbc.MysqlDataSource;
import de.Breakcraft.Survival.Commands.*;
import de.Breakcraft.Survival.Listeners.*;
import de.Breakcraft.Survival.Utils.*;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main extends JavaPlugin {
    public static DataSource dataSource;
    public static ChunkClaimManager ccm;
    public Economy econ;
    public Chat chat;
    public Permission perms;
    private static Main instance;
    public static AntiLag antiLag;
    public static BlockLog blockLog;
    public static HashMap<Player, Score[]> infos = new HashMap<Player, Score[]>();
    public List<Integer> taskIDs = new ArrayList<>();
    public static int playerCount = 0;

    @Override
    public void onEnable() {
        instance = this;

        if(!setupEconomy()) {
            this.getLogger().severe("Disabled due to no Vault dependency found!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        setupChat();
        setupPermissions();

        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUrl("jdbc:mysql://localhost:3306/Breakcraft?characterEncoding=utf8&autoReconnect=true");
        mysqlDataSource.setUser("client_minecraft");
        mysqlDataSource.setPassword("nAuE@&3d]!(h");
        dataSource = mysqlDataSource;

        getCommand("pawnshop").setExecutor(new Pawnshop());
        getCommand("craft").setExecutor(new Craft());
        getCommand("chunk").setExecutor(new Chunk());
        getCommand("chunk").setTabCompleter(new Chunk());
        getCommand("regeln").setExecutor(new Regeln());

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new PlayerJoinListener(), this);
        pm.registerEvents(new InventoryListener(), this);
        pm.registerEvents(new ChunkClaimListeners(), this);
        pm.registerEvents(new MessageListener(), this);

        ccm = new ChunkClaimManager();
        ccm.initManager();

        antiLag = new AntiLag();
        antiLag.init();

        blockLog = new BlockLog();
        blockLog.init();

        setUpScreensScheduler();
        setUpMySQLScheduler();
    }

    @Override
    public void onDisable() {
        for(int i : taskIDs) {
            Bukkit.getScheduler().cancelTask(i);
        }
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public static Main getInstance() {
        return instance;
    }

    private void setUpScreensScheduler() {
        int i = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                if(Bukkit.getOnlinePlayers().size() > 0) {
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        Scoreboard board = p.getScoreboard();
                        Objective objective = (Objective) board.getObjectives().toArray()[0];
                        double money = Main.getInstance().econ.getBalance(p);
                        Score balance = objective.getScore("   §e" + money + " €");
                        boolean isUpdated = false;
                        if (!balance.isScoreSet()) {
                            board.resetScores(infos.get(p)[1].getEntry());
                            balance.setScore(2);
                            Score[] replace = {infos.get(p)[0], balance};
                            infos.replace(p, replace);
                            isUpdated = true;
                        }

                        Score players = objective.getScore("   §e" + playerCount + " §b/ §e1000");
                        if (!players.isScoreSet()) {
                            board.resetScores(infos.get(p)[0].getEntry());
                            players.setScore(8);
                            Score[] replace = {players, infos.get(p)[1]};
                            infos.replace(p, replace);
                            isUpdated = true;
                        }

                        if (isUpdated) p.setScoreboard(board);

                    }
                }
            }
        }, 0, 20);
        taskIDs.add(i);
    }

    public void setUpMySQLScheduler() {
        int i = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                try(Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from ServerInfo WHERE id = 1")) {
                    ResultSet set = preparedStatement.executeQuery();
                    set.next();
                    Main.playerCount = Integer.parseInt(set.getString("value"));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 20*5);
        taskIDs.add(i);
    }

}
