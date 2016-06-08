package me.stuntguy3000.java.telegram.hibpbot.object;

import java.util.UUID;

import lombok.Data;
import pro.zackpollard.telegrambot.api.chat.message.Message;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardButton;
import pro.zackpollard.telegrambot.api.keyboards.InlineKeyboardMarkup;

/**
 * @author stuntguy3000
 */
@Data
public class PaginatedMessage {
    private PaginatedList paginatedList;
    private Message message;
    private UUID messageID;

    public PaginatedMessage(PaginatedList paginatedList) {
        this.paginatedList = paginatedList;
        this.messageID = UUID.randomUUID();
    }

    public InlineKeyboardMarkup getButtons() {
        InlineKeyboardButton left = InlineKeyboardButton.builder().callbackData(messageID.toString() + "|prev").text("«").build();
        InlineKeyboardButton pageIndicator = InlineKeyboardButton.builder().callbackData(messageID.toString() + "|ignore")
                .text("Page " + paginatedList.getCurrentPage() + "/" + paginatedList.getPages()).build();
        InlineKeyboardButton right = InlineKeyboardButton.builder().callbackData(messageID.toString() + "|next").text("»").build();
        return InlineKeyboardMarkup.builder().addRow(left, pageIndicator, right).build();
    }
}
