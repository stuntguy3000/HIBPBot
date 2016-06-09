package me.stuntguy3000.java.telegram.hibpbot.handler;

import java.util.ArrayList;
import java.util.List;

import me.stuntguy3000.java.telegram.hibpbot.HIBPBot;
import me.stuntguy3000.java.telegram.hibpbot.api.model.Breach;
import me.stuntguy3000.java.telegram.hibpbot.object.PaginatedMessage;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;

/**
 * @author stuntguy3000
 */
public class BreachHandler {


    public static void sendBreaches(Chat chat, List<Breach> breaches, String userID) {
        List<String> content = new ArrayList<>();

        if (breaches == null || breaches.size() == 0) {
            chat.sendMessage("No sites found for user " + userID + ".");
            return;
        }

        if (userID == null) {
            content.add("*All breached sites:*");
        } else {
            content.add("*Breached sites for user " + userID + ":*");
        }

        for (Breach breach : breaches) {
            if (breach.getDomain() == null) {
                continue;
            }
            content.add(String.format("*%s*: `%s` - %d affected users", breach.getTitle(), breach.getDomain(), breach.getPwnCount()));
        }

        if (content.size() == 1) {
            chat.sendMessage("No sites found for user " + userID + ".");
            return;
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

    public static void sendBreachInformation(Chat chat, List<Breach> breaches) {
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
