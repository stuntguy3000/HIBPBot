package me.stuntguy3000.java.telegram.hibpbot.api.exception;

/**
 * @author stuntguy3000
 */
public class NoUserException extends ApiException {
    public NoUserException() {
        super("The user could not be found.");
    }
}
