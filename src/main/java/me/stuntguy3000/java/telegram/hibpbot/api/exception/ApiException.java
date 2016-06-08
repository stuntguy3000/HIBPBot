package me.stuntguy3000.java.telegram.hibpbot.api.exception;

import lombok.Data;

/**
 * @author stuntguy3000
 */
@Data
public class ApiException extends Throwable {
    private String errorMessage;
}
