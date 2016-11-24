package me.stuntguy3000.java.telegram.hibpbot.handler;

import me.stuntguy3000.java.telegram.hibpbot.HIBPBot;
import me.stuntguy3000.java.telegram.hibpbot.api.model.Breach;
import me.stuntguy3000.java.telegram.hibpbot.hook.TelegramHook;
import me.stuntguy3000.java.telegram.hibpbot.object.PaginatedMessage;
import org.jetbrains.annotations.Nullable;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author stuntguy3000
 */
public class BreachHandler {


    public static void sendBreaches(Chat chat, List<Breach> breaches, @Nullable String userID, @Nullable Message existingMessage) {
        List<String> content = new ArrayList<>();

        if (breaches == null || breaches.size() == 0) {
            SendableTextMessage message = SendableTextMessage.builder()
                    .message("*No breaches found.*")
                    .parseMode(ParseMode.MARKDOWN)
                    .disableWebPagePreview(true)
                    .build();

            if (existingMessage == null) {
                chat.sendMessage(message);
            } else {
                TelegramHook.getBot().editMessageText(
                        existingMessage,
                        message.getMessage(),
                        ParseMode.MARKDOWN, true, null
                );
            }
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
            content.add(String.format("*%s*: `%s` - %d affected users", breach.getName(), breach.getDomain(), breach.getPwnCount()));
        }

        if (content.size() == 1) {
            SendableTextMessage message = SendableTextMessage.builder()
                    .message("No sites found for user " + userID + ".")
                    .parseMode(ParseMode.MARKDOWN)
                    .disableWebPagePreview(true)
                    .build();

            if (existingMessage == null) {
                chat.sendMessage(message);
            } else {
                TelegramHook.getBot().editMessageText(
                        existingMessage,
                        message.getMessage(),
                        ParseMode.MARKDOWN, true, null
                );
            }
            return;
        }

        PaginatedMessage paginatedMessage =
                HIBPBot.getInstance().getPaginationHandler().createPaginatedMessage(content, 15);
        Message message;

        if (existingMessage == null) {
            message = chat.sendMessage(
                    SendableTextMessage.builder()
                            .message(paginatedMessage.getPaginatedList().getCurrentPageContent())
                            .replyMarkup(paginatedMessage.getButtons())
                            .parseMode(ParseMode.MARKDOWN)
                            .disableWebPagePreview(true)
                            .build());
        } else {
            message = TelegramHook.getBot().editMessageText(
                    existingMessage,
                    paginatedMessage.getPaginatedList().getCurrentPageContent(),
                    ParseMode.MARKDOWN,
                    true,
                    paginatedMessage.getButtons()
            );
        }

        paginatedMessage.setMessage(message);
    }

    public static void sendBreachInformation(Chat chat, List<Breach> breaches, @Nullable Message existingMessage) {
        if (breaches == null || breaches.isEmpty()) {
            SendableTextMessage message = SendableTextMessage.builder()
                    .message("<b>No breaches found</b>")
                    .parseMode(ParseMode.NONE)
                    .disableWebPagePreview(true)
                    .build();

            if (existingMessage == null) {
                chat.sendMessage(message);
            } else {
                TelegramHook.getBot().editMessageText(
                        existingMessage,
                        message.getMessage(),
                        ParseMode.NONE, true, null
                );
            }
            return;
        }

        Breach breach = breaches.get(0);
        String message = String.format(
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
                breach.getDescription());

        if (existingMessage == null) {
            chat.sendMessage(
                    SendableTextMessage.builder().message(message)
                            .parseMode(ParseMode.NONE)
                            .disableWebPagePreview(true)
                            .build()
            );
        } else {
            TelegramHook.getBot().editMessageText(
                    existingMessage,
                    message,
                    ParseMode.NONE, true, null
            );
        }
    }
}
