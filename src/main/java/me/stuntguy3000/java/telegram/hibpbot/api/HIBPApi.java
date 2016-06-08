package me.stuntguy3000.java.telegram.hibpbot.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.stuntguy3000.java.telegram.hibpbot.api.exception.ApiException;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.ApiUnirestException;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.InvalidAPIRequestException;
import me.stuntguy3000.java.telegram.hibpbot.api.model.Breach;

/**
 * Represents the class used to connect with the HIBP Api
 *
 * @author stuntguy3000
 */
public class HIBPApi {

    private static String BASE_URL = "https://haveibeenpwned.com/api/";
    private static int API_VERSION = 2;
    private static HashMap<String, String> HEADERS;
    private static Gson GSON = new GsonBuilder().create();

    /**
     * Initialize headers HashMap
     */
    static {
        HEADERS = new HashMap<>();

        HEADERS.put("api-version", String.valueOf(API_VERSION));
        HEADERS.put("user-agent", "HIBPBot-Telegram-by-@stuntguy3000");
    }

    /**
     * Return an instance of Breaches, which contains all breaches, either specified by no arguments
     * or searched by a domain
     *
     * @param domain String (optional) a domain to be searched for
     *
     * @return Breaches a class containing all breaches
     */
    public static List<Breach> getBreachList(String domain) throws ApiException {
        HttpResponse<JsonNode> jsonResponse = getJson("breaches" + (domain != null ? domain : ""));

        List<Breach> breaches = new ArrayList<>();

        for (Object jsonArray : jsonResponse.getBody().getArray()) {
            String jsonArrayString = jsonArray.toString();
            breaches.add(GSON.fromJson(jsonArrayString, Breach.class));
        }

        return breaches;
    }

    public static List<Breach> getUserBreaches(String email) throws ApiException {
        return null;
    }


    /**
     * Use's Unirest to return a HttpResponse following the specified parameters
     *
     * @param params String specified parameters, which are appended onto the BASE_URL
     *
     * @return HttpResponse the response from Unirest
     */
    private static HttpResponse<JsonNode> getJson(String params) throws ApiException {
        if (params == null || params.isEmpty()) {
            throw new InvalidAPIRequestException();
        }

        Unirest.setHttpClient(makeClient());

        try {
            return Unirest.get(BASE_URL + params).headers(HEADERS).asJson();
        } catch (UnirestException e) {
            throw new ApiUnirestException(e);
        }
    }

    public static HttpClient makeClient() {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        try {
            schemeRegistry.register(new Scheme("https", 443, new MockSSLSocketFactory()));
        } catch (KeyManagementException | NoSuchAlgorithmException | UnrecoverableKeyException | KeyStoreException e) {
            e.printStackTrace();
        }
        ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
        return new DefaultHttpClient(cm);
    }
}
