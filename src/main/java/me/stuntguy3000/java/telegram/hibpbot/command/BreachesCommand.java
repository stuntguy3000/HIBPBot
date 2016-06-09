package me.stuntguy3000.java.telegram.hibpbot.command;


import java.util.List;

import me.stuntguy3000.java.telegram.hibpbot.HIBPBot;
import me.stuntguy3000.java.telegram.hibpbot.api.HIBPApi;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.ApiException;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.NoBreachesException;
import me.stuntguy3000.java.telegram.hibpbot.api.model.Breach;
import me.stuntguy3000.java.telegram.hibpbot.handler.BreachHandler;
import me.stuntguy3000.java.telegram.hibpbot.object.Util;
import me.stuntguy3000.java.telegram.hibpbot.object.command.Command;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * @author stuntguy3000
 */
public class BreachesCommand extends Command {
    public BreachesCommand() {
        super(HIBPBot.getInstance(), "[site] List all breaches in the HIBP database.", false, "breaches");
    }

    @Override
    public void processCommand(CommandMessageReceivedEvent event) throws ApiException {
        event.getChat().sendMessage("Fetching...");

        if (event.getArgs().length > 0) {
            String domain = event.getArgs()[0];

            if (Util.isValidURL(domain)) {
                List<Breach> breaches = HIBPApi.getBreachList(event.getArgs()[0]);

                if (breaches == null) {
                    event.getChat().sendMessage("No site found.");
                } else {
                    BreachHandler.sendBreaches(event.getChat(), breaches, null);
                }
            } else {
                event.getChat().sendMessage("Domain name contains invalid characters.");
            }
        } else {
            List<Breach> breachList = HIBPApi.getBreachList(null);

            if (breachList == null) {
                throw new NoBreachesException();
            } else {
                BreachHandler.sendBreaches(event.getChat(), breachList, null);
            }
        }
    }
}
