/*
 * MIT License
 *
 * Copyright (c) 2016 Luke Anderson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.stuntguy3000.java.telegram.hibpbot.handler;

import java.util.UUID;

import me.stuntguy3000.java.telegram.hibpbot.HIBPBot;
import me.stuntguy3000.java.telegram.hibpbot.hook.TelegramHook;
import me.stuntguy3000.java.telegram.hibpbot.object.PaginatedMessage;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.CallbackQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * Handles various Telegram events
 *
 * @author stuntguy3000
 */
public class TelegramEventHandler implements Listener {

    /**
     * Represents when a Command Message is received
     *
     * @param event CommandMessageReceivedEvent the event which was received
     */
    @Override
    public void onCommandMessageReceived(CommandMessageReceivedEvent event) {
        HIBPBot.getInstance().getCommandHandler().executeCommand(event.getCommand(), event);
    }

    @Override
    public void onCallbackQueryReceivedEvent(CallbackQueryReceivedEvent event) {
        String ID = event.getCallbackQuery().getData();
        System.out.println("Callback: " + ID);

        String action;
        UUID uuid;
        try {
            uuid = UUID.fromString(ID.split("\\|")[0]);
            action = ID.split("\\|")[1];
        } catch (Exception ex) {
            event.getCallbackQuery().answer("Unable to continue! Contact @stuntguy3000", true);
            ex.printStackTrace();
            return;
        }

        PaginatedMessage paginatedMessage = HIBPBot.getInstance().getPaginationHandler().getMessage(uuid);
        String content;

        if (paginatedMessage == null) {
            event.getCallbackQuery().answer("Unable to continue! Contact @stuntguy3000", true);
            return;
        } else {
            switch (action) {
                case "next": {
                    content = paginatedMessage.getPaginatedList().switchToNextPage();
                    break;
                }
                case "prev": {
                    content = paginatedMessage.getPaginatedList().switchToPreviousPage();
                    break;
                }
                case "ignore": {
                    event.getCallbackQuery().answer("Use Next or Previous to navigate.", true);
                    return;
                }
                default: {
                    event.getCallbackQuery().answer("Unable to continue! Contact @stuntguy3000", true);
                    return;
                }
            }
        }

        TelegramHook.getBot().editMessageText(
                paginatedMessage.getMessage(), content, ParseMode.MARKDOWN, false, paginatedMessage.getButtons()
        );
    }
}
