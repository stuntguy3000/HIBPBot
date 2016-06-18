package me.stuntguy3000.java.telegram.hibpbot.handler;

import java.util.HashMap;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import pro.zackpollard.telegrambot.api.user.User;

/**
 * @author stuntguy3000
 */
@Data
public class DeepLinkHandler {
    private HashMap<UUID, DeepLinkInformation> deepLinkInformation = new HashMap<>();

    public UUID addLink(User user, String input) {
        UUID uuid = UUID.randomUUID();

        deepLinkInformation.put(uuid, new DeepLinkInformation(user, input));

        return uuid;
    }

    public DeepLinkInformation getLink(UUID uuid) {
        return deepLinkInformation.get(uuid);
    }

    @AllArgsConstructor
    @Data
    private class DeepLinkInformation {
        private User user;
        private String input;
    }
}
