package me.stuntguy3000.java.telegram.hibpbot.command;


import java.util.ArrayList;
import java.util.List;

import me.stuntguy3000.java.telegram.hibpbot.HIBPBot;
import me.stuntguy3000.java.telegram.hibpbot.api.HIBPApi;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.ApiException;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.NoBreachesException;
import me.stuntguy3000.java.telegram.hibpbot.api.model.Breach;
import me.stuntguy3000.java.telegram.hibpbot.object.PaginatedMessage;
import me.stuntguy3000.java.telegram.hibpbot.object.Util;
import me.stuntguy3000.java.telegram.hibpbot.object.command.Command;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * @author stuntguy3000
 */
public class ListBreachesCommand extends Command {
    public ListBreachesCommand() {
        super(HIBPBot.getInstance(), "[site] List all breaches in the HIBP database.", false, "list", "listall", "breaches");
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
                    sendBreaches(event.getChat(), breaches);
                }
            } else {
                event.getChat().sendMessage("Domain name contains invalid characters.");
            }
        } else {
            List<Breach> breachList = HIBPApi.getBreachList(null);

            if (breachList == null) {
                throw new NoBreachesException();
            } else {
                sendBreaches(event.getChat(), breachList);
            }
        }
    }

    private void sendBreaches(Chat chat, List<Breach> breaches) {
        List<String> content = new ArrayList<>();

        if (breaches == null || breaches.size() == 0) {
            chat.sendMessage("No site found.");
            return;
        }

        for (Breach breach : breaches) {
            content.add(String.format("*%s*: `%s` - %d affected users", breach.getTitle(), breach.getDomain(), breach.getPwnCount()));
        }

        PaginatedMessage paginatedMessage =
                HIBPBot.getInstance().getPaginationHandler().createPaginatedMessage(content, 15);

        Message message = chat.sendMessage(
                SendableTextMessage.builder()
                        .message(paginatedMessage.getPaginatedList().getCurrentPageContent())
                        .replyMarkup(paginatedMessage.getButtons())
                        .parseMode(ParseMode.MARKDOWN)
                        .disableWebPagePreview(true)
                        .build());

        paginatedMessage.setMessage(message);
    }
}
