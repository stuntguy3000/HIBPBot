package me.stuntguy3000.java.telegram.hibpbot.api.model;

import java.util.List;

import lombok.Data;

/**
 * Represented a breached website or service
 *
 * @author stuntguy3000
 */
@Data
public class Breach {
    private String Name;
    private String Title;
    private String Domain;
    private String AddedDate;
    private String BreachDate;
    private long PwnCount;
    private String Description;
    private List<String> DataClasses;
    private boolean IsSensitive;
    private boolean IsActive;
    private boolean IsVerified;
}
