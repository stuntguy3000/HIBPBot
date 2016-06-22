package me.stuntguy3000.java.telegram.hibpbot;

import lombok.Data;
import me.stuntguy3000.java.telegram.hibpbot.api.HIBPApi;
import me.stuntguy3000.java.telegram.hibpbot.handler.CommandHandler;
import me.stuntguy3000.java.telegram.hibpbot.handler.ConfigHandler;
import me.stuntguy3000.java.telegram.hibpbot.handler.DeepLinkHandler;
import me.stuntguy3000.java.telegram.hibpbot.handler.JenkinsUpdateHandler;
import me.stuntguy3000.java.telegram.hibpbot.handler.LogHandler;
import me.stuntguy3000.java.telegram.hibpbot.handler.PaginationHandler;
import me.stuntguy3000.java.telegram.hibpbot.hook.TelegramHook;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;

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
    private boolean developmentMode = false;
    /*
        Handlers
     */
    private ConfigHandler configHandler;
    private CommandHandler commandHandler;
    private PaginationHandler paginationHandler;
    private DeepLinkHandler deepLinkHandler;
    private JenkinsUpdateHandler jenkinsUpdateHandler;
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
        paginationHandler = new PaginationHandler();
        configHandler = new ConfigHandler();
        commandHandler = new CommandHandler();
        deepLinkHandler = new DeepLinkHandler();

        hibpApi = new HIBPApi();

        LogHandler.log("======================================");
        LogHandler.log(" HIBPBot build by @stuntguy3000");
        LogHandler.log("======================================");

        if (this.getConfigHandler().getBotSettings().getAutoUpdater()) {
            LogHandler.log("Starting auto updater...");
            jenkinsUpdateHandler = new JenkinsUpdateHandler(
                    "HIBPBot", "http://ci.zackpollard.pro/job/",
                    "HIBPBot.jar", 60000
            );

            try {
                jenkinsUpdateHandler.startUpdater();
            } catch (JenkinsUpdateHandler.JenkinsUpdateException e) {
                e.printStackTrace();
            }
        } else {
            LogHandler.log("** Auto Updater is set to false **");
        }

        connectTelegram();

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
            TelegramHook.getBot().getChat(adminID).sendMessage(
                    SendableTextMessage.builder().message(message).parseMode(ParseMode.MARKDOWN).build());
        }
    }
}
    