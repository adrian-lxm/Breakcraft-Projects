package de.Breakcraft.Bot;

import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BotData {
    public List<Long> autoroles;
    public HashMap<Long, Integer> messages;
    public HashMap<Long, Integer> levels;
    public HashMap<Integer, Long> awards;
    public String token;
    private static Gson gson = new Gson();


    public static BotData initData() {
        if(new File("data.json").exists()) {
            try {
                BotData data = gson.fromJson(new FileReader("data.json"), BotData.class);
                System.out.println("Data loaded from Json !");
                return data;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("Data reading failed !");
                return null;
            }
        } else {
            BotData data = new BotData();
            data.autoroles = new ArrayList<>();
            data.levels = new HashMap<>();
            data.awards = new HashMap<>();
            data.messages = new HashMap<>();
            System.out.println("Data created !");
            return data;
        }
    }

    public void save() {
        String jsonString = gson.toJson(this, BotData.class);
        File file = new File("data.json");
        if(file.exists()) file.delete();
        try {
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(jsonString);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
