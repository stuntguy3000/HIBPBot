package me.stuntguy3000.java.telegram.hibpbot.hook;

import lombok.Getter;
import me.stuntguy3000.java.telegram.hibpbot.HIBPBot;
import me.stuntguy3000.java.telegram.hibpbot.command.ListBreachesCommand;
import me.stuntguy3000.java.telegram.hibpbot.handler.CommandHandler;
import me.stuntguy3000.java.telegram.hibpbot.handler.LogHandler;
import me.stuntguy3000.java.telegram.hibpbot.handler.TelegramEventHandler;
import pro.zackpollard.telegrambot.api.TelegramBot;

// @author Luke Anderson | stuntguy3000
public class TelegramHook {
    @Getter
    private static TelegramBot bot;
    @Getter
    private final HIBPBot instance;

    public TelegramHook(String authKey, HIBPBot instance) {
        this.instance = instance;

        bot = TelegramBot.login(authKey);
        bot.startUpdates(false);
        bot.getEventsManager().register(new TelegramEventHandler());
        LogHandler.log("Connected to Telegram.");

        instance.sendToAdmins("Bot has connected, running build #" + instance.getCurrentBuild());

        registerCommands();
    }

    private void registerCommands() {
        CommandHandler commandHandler = instance.getCommandHandler();

        commandHandler.registerCommand(new ListBreachesCommand());
    }
}
    