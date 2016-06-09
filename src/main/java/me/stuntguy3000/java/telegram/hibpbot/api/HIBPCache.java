package me.stuntguy3000.java.telegram.hibpbot.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Data;
import me.stuntguy3000.java.telegram.hibpbot.api.model.Breach;

/**
 * @author stuntguy3000
 */
@Data
public class HIBPCache {
    private HashMap<String, List<Breach>> cachedBreachesByID = new HashMap<>();
    private List<Breach> cachedBreaches = new ArrayList<>();

    public List<Breach> getUserCache(String user) {
        if (cachedBreachesByID.containsKey(user.toLowerCase())) {
            return cachedBreachesByID.get(user.toLowerCase());
        } else {
            return null;
        }
    }

    public void addUser(String user, List<Breach> breaches) {
        cachedBreachesByID.put(user.toLowerCase(), breaches);
    }

    public List<Breach> getDomainBreaches(String domain) {
        return cachedBreachesByID.get(domain.toLowerCase());
    }

    public void addDomain(String domain, List<Breach> breaches) {
        cachedBreachesByID.put(domain.toLowerCase(), breaches);
    }
}
