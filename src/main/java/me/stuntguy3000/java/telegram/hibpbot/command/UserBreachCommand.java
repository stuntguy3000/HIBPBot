package me.stuntguy3000.java.telegram.hibpbot.command;

import java.util.List;

import me.stuntguy3000.java.telegram.hibpbot.HIBPBot;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.ApiException;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.NoBreachesException;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.NoUserException;
import me.stuntguy3000.java.telegram.hibpbot.api.model.Breach;
import me.stuntguy3000.java.telegram.hibpbot.handler.BreachHandler;
import me.stuntguy3000.java.telegram.hibpbot.hook.TelegramHook;
import me.stuntguy3000.java.telegram.hibpbot.object.Util;
import me.stuntguy3000.java.telegram.hibpbot.object.command.Command;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * @author stuntguy3000
 */
public class UserBreachCommand extends Command {

    public UserBreachCommand() {
        super(HIBPBot.getInstance(), "<email|username> See if a user is in the HIBP database.", false, "user", "userbreach");
    }

    @Override
    public void processCommand(CommandMessageReceivedEvent event) throws ApiException {
        Message message = event.getChat().sendMessage("One moment please...");

        if (event.getArgs().length > 0) {
            String userID = event.getArgs()[0];

            if (Util.isValidUsername(userID)) {
                try {
                    List<Breach> breaches = HIBPBot.getInstance().getHibpApi().getUserBreaches(userID);
                    BreachHandler.sendBreaches(event.getChat(), breaches, userID, message);
                } catch (NoBreachesException | NoUserException ex) {
                    TelegramHook.getBot().editMessageText(
                            message, "No breaches could be found for this user.",
                            ParseMode.MARKDOWN, true, null
                    );
                }
            } else {
                TelegramHook.getBot().editMessageText(
                        message, "The username or email address is invalid.\n\nPlease contact @stuntguy3000 if this is a mistake.",
                        ParseMode.MARKDOWN, true, null
                );
            }
        } else {
            TelegramHook.getBot().editMessageText(
                    message, "Please specify a username or email address.",
                    ParseMode.MARKDOWN, true, null
            );
        }
    }
}
