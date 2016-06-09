package me.stuntguy3000.java.telegram.hibpbot.api.exception;

/**
 * @author stuntguy3000
 */
public class InvalidAPIRequestException extends ApiException {

    public InvalidAPIRequestException() {
        super("An invalid API request was performed.");
    }
}
