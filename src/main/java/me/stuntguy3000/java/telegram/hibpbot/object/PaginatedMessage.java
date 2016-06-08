package me.stuntguy3000.java.telegram.hibpbot.object;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardButton;

/**
 * @author stuntguy3000
 */
@Data
@AllArgsConstructor
public class PaginatedMessage {
    private PaginatedList paginatedList;
    private Message message;
    private UUID messageID;
    private List<InlineKeyboardButton> inlineKeyboardButtons;
}
