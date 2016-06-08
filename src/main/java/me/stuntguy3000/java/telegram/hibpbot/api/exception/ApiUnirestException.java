package me.stuntguy3000.java.telegram.hibpbot.api.exception;

import com.mashape.unirest.http.exceptions.UnirestException;

import lombok.Data;

/**
 * @author stuntguy3000
 */
@Data
public class ApiUnirestException extends ApiException {
    private UnirestException unirestException;

    public ApiUnirestException(UnirestException unirestException) {
        this.unirestException = unirestException;
    }
}
