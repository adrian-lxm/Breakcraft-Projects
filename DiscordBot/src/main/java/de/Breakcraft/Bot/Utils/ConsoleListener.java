package de.Breakcraft.Bot.Utils;

import de.Breakcraft.Bot.BotMain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleListener extends Thread {

    @Override
    public void run() {
        boolean isRunning = true;
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line;
        while(isRunning) {
            try {
                line = reader.readLine();
                if(line != null) {
                    switch (line) {
                        case "shutdown":
                            SQLReviewer.instance.stop();
                            ContractListener.instance.stop();
                            BotMain.instance.shutdown();
                            break;
                        default:
                            break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}