package me.stuntguy3000.java.telegram.hibpbot.api.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author stuntguy3000
 */
@Data
@AllArgsConstructor
public class ApiException extends Throwable {
    private String errorMessage;
}
