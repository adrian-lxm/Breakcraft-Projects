package de.Breakcraft.Bot.Utils;

import com.google.gson.Gson;
import de.Breakcraft.Bot.BotMain;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ContractListener extends TimerTask {
    public static ContractListener instance;
    private Timer timer;
    private Gson gson;

    public ContractListener() {
        instance = this;
        timer = new Timer();
        gson = new Gson();
    }
    
    @Override
    public void run() {
        try(Connection connection = BotMain.instance.dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement("select * from Contracts where `sent` = ?")) {
            preparedStatement.setBoolean(1, false);
            ResultSet set = preparedStatement.executeQuery();
            while(set.next()) {
                int id = set.getInt("id");
                String title = set.getString("title");
                List<MessageEmbed.Field> fields = convertJsonToFields(set.getString("fields"));
                long role = set.getLong("role");
                long channel = set.getLong("channel");
                Contract contract = new Contract(id, title, fields, role, channel);
                BotMain.contracts.add(contract);
                contract.send();
            }
            set.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        timer.schedule(this, 30000, 30000);
    }

    public void stop() {
        timer.cancel();
    }

    private List<MessageEmbed.Field> convertJsonToFields(String json) {
        String[][] rawFields = gson.fromJson(json, String[][].class);
        List<MessageEmbed.Field> fields = new ArrayList<>();
        for(int i = 0; i < rawFields.length; i++) {
            fields.add(new MessageEmbed.Field(rawFields[i][0], rawFields[i][1], false, false));
        }
        return fields;
    }

}
