package me.stuntguy3000.java.telegram.hibpbot.handler;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import lombok.Data;
import me.stuntguy3000.java.telegram.hibpbot.object.PaginatedList;
import me.stuntguy3000.java.telegram.hibpbot.object.PaginatedMessage;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardButton;

/**
 * @author stuntguy3000
 */
@Data
public class PaginationHandler {
    private HashMap<UUID, PaginatedMessage> paginatedMessages = new HashMap<>();

    public PaginatedMessage createPaginatedMessage(Message message, UUID uuid, List<String> content, int perPage, List<InlineKeyboardButton> buttons) {
        PaginatedList paginatedList = new PaginatedList(content, perPage);

        PaginatedMessage paginatedMessage = new PaginatedMessage(paginatedList, message, uuid, buttons);
        paginatedMessages.put(uuid, paginatedMessage);
        return paginatedMessage;
    }
}
