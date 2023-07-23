package de.Breakcraft.Citybuild;

import de.Breakcraft.Citybuild.Commands.*;
import de.Breakcraft.Citybuild.Utils.*;
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

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main extends JavaPlugin {
    public static final String PREFIX = "[§5Breakcraft§f]";
    public static Connection connection;
    public Economy econ;
    public Chat chat;
    public Permission perms;
    private static Main instance;
    public static AntiLag antiLag;
    public static HashMap<Player, Score[]> infos = new HashMap<Player, Score[]>();
    public List<Integer> taskIDs = new ArrayList<>();
    public WorldManager worldManager = new WorldManager();
    private final String sqlUsername = "";
    private final String sqlPassword = "";

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

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Breakcraft?autoReconnect=true", sqlUsername, sqlPassword);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        getCommand("pawnshop").setExecutor(new Pawnshop());
        getCommand("craft").setExecutor(new Craft());

        PluginManager pm = Bukkit.getPluginManager();

        setUpScreensScheduler();
        worldManager.init();
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
                    int playerCount = 0;
                    try {
                        String sql = "SELECT * from ServerInfo WHERE id = 1";
                        PreparedStatement preparedStatement = Main.connection.prepareStatement(sql);
                        ResultSet set = preparedStatement.executeQuery(sql);
                        set.next();
                        playerCount = Integer.parseInt(set.getString("value"));
                        set.close();
                        preparedStatement.close();
                    } catch (SQLException e2) {
                        e2.printStackTrace();
                        try {
                            Main.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Breakcraft?autoReconnect=true", "client_minecraft", "nAuE@&3d]!(h");
                            System.out.println("Connection rebuild");
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
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

}
