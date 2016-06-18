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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.stuntguy3000.java.telegram.hibpbot.HIBPBot;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.ApiException;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.InvalidAPIRequestException;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.NoUserException;
import me.stuntguy3000.java.telegram.hibpbot.api.model.Breach;
import me.stuntguy3000.java.telegram.hibpbot.hook.TelegramHook;
import me.stuntguy3000.java.telegram.hibpbot.object.PaginatedMessage;
import me.stuntguy3000.java.telegram.hibpbot.object.Util;
import pro.zackpollard.telegrambot.api.chat.inline.send.InlineQueryResponse;
import pro.zackpollard.telegrambot.api.chat.inline.send.content.InputTextMessageContent;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResult;
import pro.zackpollard.telegrambot.api.chat.inline.send.results.InlineQueryResultArticle;
import pro.zackpollard.telegrambot.api.chat.message.send.ParseMode;
import pro.zackpollard.telegrambot.api.event.Listener;
import pro.zackpollard.telegrambot.api.event.chat.CallbackQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.inline.InlineQueryReceivedEvent;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * Handles various Telegram events
 *
 * @author stuntguy3000
 */
public class TelegramEventHandler implements Listener {

    private URL IMAGE_RED_URL;
    private URL IMAGE_GREEN_URL;
    private URL IMAGE_BLUE_URL;

    public TelegramEventHandler() {
        try {
            IMAGE_GREEN_URL = new URL("https://i.imgur.com/tSqwfQf.png");
            IMAGE_RED_URL = new URL("https://i.imgur.com/gt6uuYo.png");
            IMAGE_BLUE_URL = new URL("https://i.imgur.com/IOkP5OT.png");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
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

    @Override
    public void onInlineQueryReceived(InlineQueryReceivedEvent event) {
        String input = event.getQuery().getQuery();
        List<InlineQueryResult> inlineQueryResults = new ArrayList<>();

        try {
            if (input.contains(" ")) {
                throw new InvalidAPIRequestException();
            }

            List<Breach> breaches = HIBPBot.getInstance().getHibpApi().getUserBreaches(input);

            inlineQueryResults.add(InlineQueryResultArticle.builder()
                    .description(input + " was found in " + breaches.size() + " breach" + Util.plural("es", breaches.size()) + ". Click here to learn more.")
                    .title(breaches.size() + " breach" + Util.plural("es", breaches.size()) + " found.")
                    .thumbUrl(IMAGE_RED_URL)
                    .inputMessageContent(
                            InputTextMessageContent.builder().messageText(
                                    "(Click here to learn more...)[https://telegram.me/hibpbot?start=" +
                                            HIBPBot.getInstance().getDeepLinkHandler().addLink(event.getQuery().getSender(), input)
                                            + "]").build()
                    )
                    .id(input + "|" + UUID.randomUUID())
                    .build()
            );
        } catch (NoUserException e) {
            inlineQueryResults.add(InlineQueryResultArticle.builder()
                    .description(input + " has not been leaked.")
                    .title("No breaches found.")
                    .thumbUrl(IMAGE_GREEN_URL)
                    .inputMessageContent(
                            InputTextMessageContent.builder().parseMode(ParseMode.MARKDOWN).messageText("Congratulations, the email address or username *" + event.getQuery().getQuery() + "* has not been leaked (in any known databases to HIBP).").build()
                    )
                    .build()
            );
        } catch (InvalidAPIRequestException e) {
            inlineQueryResults.add(InlineQueryResultArticle.builder()
                    .description("You have specified an invalid username or email address.\n" +
                            "\nIf this is an error (such as the email/username is valid) please contact @stuntguy3000")
                    .title("Invalid Username/Domain")
                    .thumbUrl(IMAGE_BLUE_URL)
                    .inputMessageContent(
                            InputTextMessageContent.builder().messageText("You have specified an invalid username or domain.\n" +
                                    "\n" +
                                    "If this is an error (such as the email/username is valid) please contact @stuntguy3000").build()
                    )
                    .build()
            );
        } catch (ApiException e) {
            e.printStackTrace();

            inlineQueryResults.add(InlineQueryResultArticle.builder()
                    .description("Please try again later!")
                    .title("Unknown error occurred.")
                    .thumbUrl(IMAGE_BLUE_URL)
                    .inputMessageContent(
                            InputTextMessageContent
                                    .builder()
                                    .messageText("An unknown error occurred. Please contact @stuntguy3000")
                                    .build()
                    )
                    .build()
            );
        }

        InlineQueryResponse inlineQueryResponse = InlineQueryResponse.builder().cache_time(0).is_personal(true).results(inlineQueryResults).build();
        event.getQuery().answer(TelegramHook.getBot(), inlineQueryResponse);
    }
}
