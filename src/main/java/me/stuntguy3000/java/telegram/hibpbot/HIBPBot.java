package me.stuntguy3000.java.telegram.hibpbot;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import lombok.Data;
import me.stuntguy3000.java.telegram.hibpbot.api.HIBPApi;
import me.stuntguy3000.java.telegram.hibpbot.handler.CommandHandler;
import me.stuntguy3000.java.telegram.hibpbot.handler.ConfigHandler;
import me.stuntguy3000.java.telegram.hibpbot.handler.LogHandler;
import me.stuntguy3000.java.telegram.hibpbot.handler.PaginationHandler;
import me.stuntguy3000.java.telegram.hibpbot.handler.UpdateHandler;
import me.stuntguy3000.java.telegram.hibpbot.hook.TelegramHook;

// @author Luke Anderson | stuntguy3000
@Data
public class HIBPBot {
    /*
        Instance
     */
    private static HIBPBot instance;
    /*
        Runtime Build Options, set by configuration
     */
    private int currentBuild = 0;
    private boolean developmentMode = false;
    /*
        Handlers
     */
    private ConfigHandler configHandler;
    private CommandHandler commandHandler;
    private PaginationHandler paginationHandler;
    private HIBPApi hibpApi;

    public static HIBPBot getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        new HIBPBot().main();
    }

    private void connectTelegram() {
        LogHandler.log("Connecting to Telegram...");
        new TelegramHook(configHandler.getBotSettings().getTelegramKey(), this);
    }

    public void main() {
        instance = this;
        configHandler = new ConfigHandler();
        commandHandler = new CommandHandler();
        paginationHandler = new PaginationHandler();

        hibpApi = new HIBPApi();

        File build = new File("build");

        if (!build.exists()) {
            try {
                build.createNewFile();
                PrintWriter writer = new PrintWriter(build, "UTF-8");
                writer.print(0);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            currentBuild = Integer.parseInt(FileUtils.readFileToString(build));
        } catch (IOException e) {
            e.printStackTrace();
        }

        LogHandler.log("======================================");
        LogHandler.log(" HIBPBot build " + currentBuild + " by @stuntguy3000");
        LogHandler.log("======================================");

        connectTelegram();

        if (this.getConfigHandler().getBotSettings().getAutoUpdater()) {
            LogHandler.log("Starting auto updater...");
            Thread updater = new Thread(new UpdateHandler(this, "HIBPBot", "HIBPBot"));
            updater.start();
        } else {
            LogHandler.log("** Auto Updater is set to false **");
        }

        while (true) {
            String in = System.console().readLine();
            switch (in.toLowerCase()) {
                case "quit":
                case "stop":
                case "exit": {
                    System.exit(0);
                }
            }
        }
    }

    /**
     * Sends a message to all bot admins
     *
     * @param message String the message to be sent
     */
    public void sendToAdmins(String message) {
        for (int adminID : configHandler.getBotSettings().getTelegramAdmins()) {
            TelegramHook.getBot().getChat(adminID).sendMessage(message);
        }
    }
}
    