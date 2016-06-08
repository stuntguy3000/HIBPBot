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

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.ApiException;
import me.stuntguy3000.java.telegram.hibpbot.object.command.Command;
import pro.zackpollard.telegrambot.api.event.chat.message.CommandMessageReceivedEvent;

/**
 * Handles command registration and execution
 * <p>Can also generate a BotFather command String.</p>
 *
 * @author aaomidi
 * @author stuntguy3000
 */
@Data
public class CommandHandler {
    public HashMap<String[], Command> commands = new HashMap<>();

    /**
     * Execute a command
     *
     * @param command String the name of the command
     * @param event   CommandMessageReceivedEvent the event in which the command was instructed to
     *                be called
     */
    public void executeCommand(String command, CommandMessageReceivedEvent event) {
        Command cmd = null;

        for (Map.Entry<String[], Command> commandInstance : commands.entrySet()) {
            for (String name : commandInstance.getKey()) {
                if (command.equalsIgnoreCase(name)) {
                    cmd = commandInstance.getValue();
                }
            }
        }

        if (cmd != null) {
            try {
                cmd.processCommand(event);
            } catch (ApiException e) {
                event.getChat().sendMessage("An unexpected API error occurred. Try again later.");
                //HIBPBot.getInstance().sendToAdmins(e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Generate a command list for Telegram's Bot Father protocol
     * <p>Allows Telegram clients to see a list of commands</p>
     *
     * @return String the list of commands accepted by botfather
     */
    public String getBotFatherString() {
        StringBuilder sb = new StringBuilder();
        for (Command cmd : commands.values()) {
            sb.append(cmd.createBotFatherString()).append("\n");
        }

        return sb.toString();
    }

    /**
     * Register a new command
     *
     * @param cmd Command the command to be registered
     */
    public void registerCommand(Command cmd) {
        commands.put(cmd.getNames(), cmd);
    }
}
