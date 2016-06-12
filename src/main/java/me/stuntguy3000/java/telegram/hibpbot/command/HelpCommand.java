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
public class HelpCommand extends Command {

    public HelpCommand() {
        super(HIBPBot.getInstance(), "/help View help information", false, "help", "start");
    }

    public void processCommand(CommandMessageReceivedEvent event) {
        Chat chat = event.getChat();

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("*HIBPBot* is a Telegram implementation of http://www.haveibeenpwned.com which is a free service allowing anyone to see if an email or username has been leaked in recent significant database breaches.\n\n");
        stringBuilder.append("*Command List*:\n");

        for (Command command : HIBPBot.getInstance().getCommandHandler().getCommands().values()) {
            stringBuilder.append(String.format("/%s - %s", command.getNames()[0], getDescription()));
        }

        chat.sendMessage(SendableTextMessage.builder().message(
                stringBuilder.toString()).parseMode(ParseMode.MARKDOWN)
                .disableWebPagePreview(true).build());
    }
}
