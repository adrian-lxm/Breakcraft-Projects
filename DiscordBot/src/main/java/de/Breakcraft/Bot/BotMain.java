package de.Breakcraft.Bot;

import com.mysql.cj.jdbc.MysqlDataSource;
import de.Breakcraft.Bot.Utils.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BotMain {
    public static BotMain instance;
    public JDA jda;
    public BotData data;
    public DataSource dataSource;
    public static long supportMsg;
    public static List<Contract> contracts = new ArrayList<>();
    private static final String sqlUsername = "";
    private static final String sqlPassword = "";

    public BotMain() throws LoginException {
        data = BotData.initData();
        System.out.println("Data applied !");
        JDABuilder builder = JDABuilder.createDefault(data.token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("Minecraft auf Breakcraft.de"));
        builder.addEventListeners(new Listener());
        builder.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_PRESENCES);
        builder.setAutoReconnect(true);
        jda = builder.build();
    }

    public static void main(String[] args) throws LoginException {
        instance = new BotMain();

        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setUrl("jdbc:mysql://localhost:3306/Breakcraft?characterEncoding=utf8&autoReconnect=true");
        mysqlDataSource.setUser(sqlUsername);
        mysqlDataSource.setPassword(sqlPassword);
        instance.dataSource = mysqlDataSource;
    }

    public void shutdown() {
        jda.getPresence().setStatus(OnlineStatus.OFFLINE);
        jda.shutdown();
        data.save();
        try {
            dataSource.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.exit(0);
    }

}
