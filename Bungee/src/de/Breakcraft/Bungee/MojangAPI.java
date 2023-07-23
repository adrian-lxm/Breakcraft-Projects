package de.Breakcraft.Bungee;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class MojangAPI {

    private static Gson gson = new Gson();

    public static UUID getUUIDByUsername(String username) {
        try {
            URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            MojangResponse response = gson.fromJson(reader.readLine(), MojangResponse.class);
            reader.close();
            if(response.id == null) return null;
            return fromTrimmed(response.id);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static UUID fromTrimmed(String trimmedUUID) {
        StringBuilder builder = new StringBuilder(trimmedUUID.trim());
        try {
            builder.insert(20, "-");
            builder.insert(16, "-");
            builder.insert(12, "-");
            builder.insert(8, "-");
        } catch (StringIndexOutOfBoundsException e){
            throw new IllegalArgumentException();
        }

        return UUID.fromString(builder.toString());
    }

    class MojangResponse {
        public String name;
        public String id;
        public String error;
        public String errorMessage;
    }

}
