package me.stuntguy3000.java.telegram.hibpbot.api.exception;

/**
 * @author stuntguy3000
 */
public class NoBreachesException extends ApiException {
    public NoBreachesException() {
        super("No breaches could be found.");
    }
}
