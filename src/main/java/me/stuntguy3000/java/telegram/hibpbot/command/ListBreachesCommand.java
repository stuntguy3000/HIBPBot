package me.stuntguy3000.java.telegram.hibpbot.command;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import me.stuntguy3000.java.telegram.hibpbot.HIBPBot;
import me.stuntguy3000.java.telegram.hibpbot.api.HIBPApi;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.ApiException;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.NoBreachesException;
import me.stuntguy3000.java.telegram.hibpbot.api.model.Breach;
import me.stuntguy3000.java.telegram.hibpbot.object.PaginatedMessage;
import me.stuntguy3000.java.telegram.hibpbot.object.command.Command;
import pro.zackpollard.telegrambot.api.chat.Chat;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.chat.message.send.SendableTextMessage;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardButton;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardMarkup;

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
            List<Breach> breaches = HIBPApi.getBreachList(event.getArgs()[0]);

            if (breaches == null) {
                event.getChat().sendMessage("No site found.");
            } else {
                sendBreaches(event.getChat(), breaches);
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

        for (Breach breach : breaches) {
            content.add(breach.getName());
        }

        UUID uuid = UUID.randomUUID();

        InlineKeyboardButton left = InlineKeyboardButton.builder().callbackData(uuid.toString() + "|prev").text("«").build();
        InlineKeyboardButton right = InlineKeyboardButton.builder().callbackData(uuid.toString() + "|next").text("»").build();
        InlineKeyboardMarkup pagination = InlineKeyboardMarkup.builder().addRow(left, right).build();
        Message message = chat.sendMessage(SendableTextMessage.builder().message("This is paginated!").replyMarkup(pagination).build());

        PaginatedMessage paginatedMessage = HIBPBot.getInstance().getPaginationHandler().createPaginatedMessage(
                message, UUID.randomUUID(), content, 15, Arrays.asList(left, right)
        );

        System.out.println(paginatedMessage.getMessageID());
    }
}
