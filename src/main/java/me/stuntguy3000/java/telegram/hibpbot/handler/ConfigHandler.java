package me.stuntguy3000.java.telegram.hibpbot.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import lombok.Getter;
import me.stuntguy3000.java.telegram.hibpbot.object.BotSettings;

// @author Luke Anderson | stuntguy3000
public class ConfigHandler {

    @Getter
    private BotSettings botSettings = new BotSettings();

    public ConfigHandler() {
        loadFile("config.json");
    }

    public void loadFile(String fileName) {
        Gson gson = new Gson();
        File configFile = new File(fileName);

        if (configFile.exists()) {
            BufferedReader br;
            try {
                br = new BufferedReader(new FileReader(configFile));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            }

            botSettings = gson.fromJson(br, BotSettings.class);
        } else {
            saveConfig(fileName);
        }
    }

    public void saveConfig(String fileName) {
        File configFile = new File(fileName);
        GsonBuilder builder = new GsonBuilder().setPrettyPrinting();
        Gson gson = builder.create();
        String json = null;

        json = gson.toJson(botSettings);

        FileOutputStream outputStream;

        try {
            if (!configFile.exists()) {
                configFile.createNewFile();
            }
            outputStream = new FileOutputStream(configFile);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LogHandler.log("The object could not be saved as the file couldn't be found on the storage device. Please check the directories read/write permissions and contact the developer!");
        } catch (IOException e) {
            e.printStackTrace();
            LogHandler.log("The object could not be written to as an error occurred. Please check the directories read/write permissions and contact the developer!");
        } catch (NullPointerException e) {
            e.printStackTrace();
            LogHandler.log("Invalid Config Specified! Please check the directories read/write permissions and contact the developer!");
        }
    }
}

    
