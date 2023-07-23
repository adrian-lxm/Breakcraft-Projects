package de.Breakcraft.Bungee;

import com.mysql.cj.jdbc.MysqlDataSource;
import de.Breakcraft.Bungee.Commands.Ban;
import de.Breakcraft.Bungee.Commands.Lobby;
import de.Breakcraft.Bungee.Commands.Tempban;
import de.Breakcraft.Bungee.Commands.Unban;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class Main extends Plugin {
    public static Main instance;
    public static DataSource dataSource;
    public static int bans = 0;
    private final String sqlUsername = "";
    private final String sqlPassword = "";

    @Override
    public void onEnable() {
        instance = this;

        new Listeners(this);
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Lobby());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Ban());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Tempban());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Unban());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new Shutdown());

        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUrl("jdbc:mysql://localhost:3306/Breakcraft?characterEncoding=utf8&autoReconnect=true");
        mysqlDataSource.setUser(sqlUsername);
        mysqlDataSource.setPassword(sqlPassword);
        dataSource = mysqlDataSource;

        try(Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select * from `Verifications`")) {
            ResultSet set = preparedStatement.executeQuery();
            while (set.next()) {
                Listeners.takedPins.add(set.getInt("pin"));
            }
            set.close();
            preparedStatement.close();
            PreparedStatement preparedStatement2 = connection.prepareStatement("select * from `Bans`");
            set = preparedStatement2.executeQuery();
            while(set.next()) bans = set.getInt("id");
            set.close();
            preparedStatement2.close();
            startBanScheduler();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static Configuration getBanConfig() {
        File file = new File(instance.getDataFolder(), "bans.yml");
        try {
            if(!file.exists()) file.createNewFile();
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveBanConfiguration(Configuration config) {
        File file = new File(instance.getDataFolder(), "bans.yml");
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startBanScheduler() {
        getProxy().getScheduler().schedule(this, new Runnable() {
            @Override
            public void run() {
                try(Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select * from `Bans` where `ends-up` != 0 && passing = 0")) {
                    ResultSet set = preparedStatement.executeQuery();
                    while(set.next()) {
                        double endsup = set.getDouble("ends-up");
                        boolean passing = set.getBoolean("passing");
                        if(endsup < System.currentTimeMillis()) {
                            PreparedStatement preparedStatement2 = connection.prepareStatement("update Bans set passing = ? where id = ?");
                            preparedStatement2.setBoolean(1, true);
                            preparedStatement2.setInt(2, set.getInt("id"));
                            preparedStatement2.executeUpdate();
                            preparedStatement2.close();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

}
