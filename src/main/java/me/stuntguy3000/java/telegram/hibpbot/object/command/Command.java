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

package me.stuntguy3000.java.telegram.hibpbot.object.command;

import java.util.Arrays;

import lombok.Data;
import me.stuntguy3000.java.telegram.hibpbot.HIBPBot;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.ApiException;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * Represents a command
 */
@Data
public abstract class Command {
    private final String[] names;
    private final String description;
    private final boolean adminOnly;
    private final HIBPBot instance;

    /**
     * Initiates a new Command
     *
     * @param instance    Telegames the instance of Telegames
     * @param description String the command description
     * @param adminOnly   Boolean true if restricted to bot admins
     * @param names       String[] aliases to trigger the command
     */
    public Command(HIBPBot instance, String description, boolean adminOnly, String... names) {
        this.instance = instance;
        this.adminOnly = adminOnly;
        this.names = names;
        this.description = description;

        instance.getCommandHandler().registerCommand(this);
    }

    /**
     * Create a Bot Father String
     *
     * @return String a command description used for Telegram's Bot Father service
     */
    public String createBotFatherString() {
        return String.format("%s - %s", Arrays.toString(names), description);
    }

    /**
     * Abstract method to process the command
     *
     * @param event CommandMessageReceivedEvent the event which caused the trigger
     */
    public abstract void processCommand(CommandMessageReceivedEvent event) throws ApiException;
}
