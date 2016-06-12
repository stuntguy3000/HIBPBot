package me.stuntguy3000.java.telegram.hibpbot.command;

import me.stuntguy3000.java.telegram.hibpbot.HIBPBot;
import me.stuntguy3000.java.telegram.hibpbot.object.command.Command;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * @author stuntguy3000
 */
public class VersionCommand extends Command {

    public VersionCommand() {
        super(HIBPBot.getInstance(), "/version View the bot's current version", false, "version", "about", "info");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        chat.sendMessage(SendableTextMessage.builder().message(
                " *HIBPBot" + (HIBPBot.getInstance().isDevelopmentMode() ? " Dev Mode " : " ") + "by* @stuntguy3000\n" +
                        "*Current version:* " + HIBPBot.getInstance().getCurrentBuild() + "\n\n" +
                        "Source [Available on GitHub](https://github.com/stuntguy3000/hibpbot)\n" +
                        "Created using @zackpollard's [JavaTelegramBotAPI](https://github.com/zackpollard/JavaTelegramBot-API)").parseMode(ParseMode.MARKDOWN).disableWebPagePreview(true).build());
    }
}
