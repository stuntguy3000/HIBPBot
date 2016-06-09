package me.stuntguy3000.java.telegram.hibpbot.command;

import java.util.List;

import me.stuntguy3000.java.telegram.hibpbot.HIBPBot;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.ApiException;
import me.stuntguy3000.java.telegram.hibpbot.api.model.Breach;
import me.stuntguy3000.java.telegram.hibpbot.handler.BreachHandler;
import me.stuntguy3000.java.telegram.hibpbot.object.command.Command;
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
        event.getChat().sendMessage("Fetching...");

        if (event.getArgs().length > 0) {
            String userID = event.getArgs()[0];

            List<Breach> breaches = HIBPBot.getInstance().getHibpApi().getUserBreaches(userID);

            if (breaches == null) {
                event.getChat().sendMessage("Good news! This email or username has not been leaked.");
            } else {
                BreachHandler.sendBreaches(event.getChat(), breaches, userID);
            }
        } else {
            event.getChat().sendMessage("Please specify an email address.");
        }
    }
}
