package me.stuntguy3000.java.telegram.hibpbot.command;

import java.util.List;

import me.stuntguy3000.java.telegram.hibpbot.HIBPBot;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.ApiException;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.NoBreachesException;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.NoUserException;
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

            try {
                List<Breach> breaches = HIBPBot.getInstance().getHibpApi().getUserBreaches(userID);
                BreachHandler.sendBreaches(event.getChat(), breaches, userID);
            } catch (NoBreachesException | NoUserException ex) {
                event.getChat().sendMessage("This user was not found in any breaches.");
            }
        } else {
            event.getChat().sendMessage("Please specify an email address.");
        }
    }
}
