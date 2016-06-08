package me.stuntguy3000.java.telegram.hibpbot.object;

import java.util.Collections;
import java.util.List;

import lombok.Getter;

// @author Luke Anderson | stuntguy3000
public class BotSettings {
    @Getter
    private Boolean autoUpdater;
    @Getter
    private List<Integer> telegramAdmins;
    @Getter
    private String telegramKey;

    public BotSettings() {
        this.telegramKey = "";
        this.telegramAdmins = Collections.singletonList(97312446);
        this.autoUpdater = false;
    }
}
    