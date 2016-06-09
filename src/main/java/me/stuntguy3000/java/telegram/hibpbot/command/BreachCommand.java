package me.stuntguy3000.java.telegram.hibpbot.command;

import java.util.List;

import me.stuntguy3000.java.telegram.hibpbot.HIBPBot;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.ApiException;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.NoBreachesException;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.NoUserException;
import me.stuntguy3000.java.telegram.hibpbot.api.model.Breach;
import me.stuntguy3000.java.telegram.hibpbot.handler.BreachHandler;
import me.stuntguy3000.java.telegram.hibpbot.object.Util;
import me.stuntguy3000.java.telegram.hibpbot.object.command.Command;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * @author stuntguy3000
 */
public class BreachCommand extends Command {

    public BreachCommand() {
        super(HIBPBot.getInstance(), "<breach> View specific information about a breach.", false, "breach");
    }

    @Override
    public void processCommand(CommandMessageReceivedEvent event) throws ApiException {
        event.getChat().sendMessage("Fetching...");

        if (event.getArgs().length > 0) {
            String domain = event.getArgs()[0];

            if (Util.isValidURL(domain)) {
                try {
                    List<Breach> breaches = HIBPBot.getInstance().getHibpApi().getBreachList(event.getArgs()[0]);
                    BreachHandler.sendBreachInformation(event.getChat(), breaches);
                } catch (NoBreachesException | NoUserException ex) {
                    event.getChat().sendMessage("No breach could be found.");
                }
            } else {
                event.getChat().sendMessage("Domain name is invalid.\n" +
                        "If this is a mistake, contact @stuntguy3000");
            }
        } else {
            event.getChat().sendMessage("Please specify a domain.");
        }
    }
}
