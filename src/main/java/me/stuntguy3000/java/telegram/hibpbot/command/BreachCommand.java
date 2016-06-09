package me.stuntguy3000.java.telegram.hibpbot.command;

import java.util.List;

import me.stuntguy3000.java.telegram.hibpbot.HIBPBot;
import me.stuntguy3000.java.telegram.hibpbot.api.HIBPApi;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.ApiException;
import me.stuntguy3000.java.telegram.hibpbot.api.model.Breach;
import me.stuntguy3000.java.telegram.hibpbot.object.Util;
import me.stuntguy3000.java.telegram.hibpbot.object.command.Command;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
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
                List<Breach> breaches = HIBPApi.getBreachList(event.getArgs()[0]);

                if (breaches == null) {
                    event.getChat().sendMessage("No site found.");
                } else {
                    sendBreachInformation(event.getChat(), breaches);
                }
            } else {
                event.getChat().sendMessage("Domain name contains invalid characters.");
            }
        } else {
            event.getChat().sendMessage("Please specify a domain.");
        }
    }

    private void sendBreachInformation(Chat chat, List<Breach> breaches) {
        if (breaches == null || breaches.isEmpty()) {
            chat.sendMessage("No site found.");
            return;
        }

        Breach breach = breaches.get(0);

        chat.sendMessage(
                SendableTextMessage.builder().message(
                        String.format(
                                "<b>Breach Information: </b>\n" +
                                        "<b>Domain: </b>%s\n" +
                                        "<b>Name: </b>%s\n" +
                                        "<b>Title: </b>%s\n" +
                                        "<b>Effected users: </b>%s\n" +
                                        "<b>Added on: </b>%s\n" +
                                        "<b>Breached on: </b>%s\n" +
                                        "<b>Leaked Data: </b>%s\n\n" +
                                        "<b>Description: </b>%s",
                                breach.getDomain(),
                                breach.getName(),
                                breach.getTitle(),
                                breach.getPwnCount(),
                                breach.getAddedDate(),
                                breach.getBreachDate(),
                                String.join(", ", breach.getDataClasses()),
                                breach.getDescription()))
                        .parseMode(ParseMode.HTML)
                        .disableWebPagePreview(true)
                        .build()
        );
    }
}
