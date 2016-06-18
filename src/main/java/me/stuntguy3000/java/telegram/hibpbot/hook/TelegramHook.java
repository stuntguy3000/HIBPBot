package me.stuntguy3000.java.telegram.hibpbot.hook;

import lombok.Getter;
import me.stuntguy3000.java.telegram.hibpbot.HIBPBot;
import me.stuntguy3000.java.telegram.hibpbot.command.BreachCommand;
import me.stuntguy3000.java.telegram.hibpbot.command.BreachesCommand;
import me.stuntguy3000.java.telegram.hibpbot.command.HelpCommand;
import me.stuntguy3000.java.telegram.hibpbot.command.UserBreachCommand;
import me.stuntguy3000.java.telegram.hibpbot.command.VersionCommand;
import me.stuntguy3000.java.telegram.hibpbot.handler.CommandHandler;
import me.stuntguy3000.java.telegram.hibpbot.handler.JenkinsUpdateHandler;
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

        JenkinsUpdateHandler.UpdateInformation updateInformation = instance.getJenkinsUpdateHandler().getLastUpdate();

        if (updateInformation == null || updateInformation.getGitCommitAuthors() == null || updateInformation.getGitCommitAuthors().isEmpty()) {
            instance.sendToAdmins("*RedditLiveBot has connected.\n\n No build information available...*");
        } else {
            instance.sendToAdmins(String.format("*RedditLiveBot has connected (Build %d).*\n\n" +
                            "*Last commit information:*\n" +
                            "*Description:* %s\n" +
                            "*Author:* %s\n" +
                            "*Commit ID:* %s\n",
                    updateInformation.getBuildNumber(),
                    updateInformation.getGitCommitMessages().get(0),
                    updateInformation.getGitCommitAuthors().get(0),
                    "[" + updateInformation.getGitCommitIds().get(0) + "]" +
                            "(https://github.com/stuntguy3000/RedditLive/commit/"
                            + updateInformation.getGitCommitIds().get(0) + ")"));
        }

        registerCommands();
    }

    private void registerCommands() {
        CommandHandler commandHandler = instance.getCommandHandler();

        commandHandler.registerCommand(new BreachesCommand());
        commandHandler.registerCommand(new BreachCommand());
        commandHandler.registerCommand(new UserBreachCommand());
        commandHandler.registerCommand(new HelpCommand());
        commandHandler.registerCommand(new VersionCommand());
    }
}
    