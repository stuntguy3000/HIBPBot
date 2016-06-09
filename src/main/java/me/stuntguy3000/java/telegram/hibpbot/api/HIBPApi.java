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

    private final HIBPCache hibpCache;
    private final String baseUrl = "https://haveibeenpwned.com/api/";
    private final int apiVersion = 2;
    private final HashMap<String, String> headers;
    private final Gson gson = new GsonBuilder().create();

    /**
     * Initialize headers HashMap
     */
    public HIBPApi() {
        headers = new HashMap<>();

        headers.put("api-version", String.valueOf(apiVersion));
        headers.put("user-agent", "HIBPBot-Telegram-by-@stuntguy3000");

        hibpCache = new HIBPCache();
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

    /**
     * Return an instance of Breaches, which contains all breaches, either specified by no arguments
     * or searched by a domain
     *
     * @param domain String (optional) a domain to be searched for
     *
     * @return Breaches a class containing all breaches
     */
    public List<Breach> getBreachList(String domain) throws ApiException {
        if (domain == null) {
            if (hibpCache.getCachedBreaches() != null) {
                return hibpCache.getCachedBreaches();
            }
        } else {
            if (hibpCache.getDomainBreaches(domain) != null) {
                return hibpCache.getDomainBreaches(domain);
            }
        }

        HttpResponse<JsonNode> jsonResponse = getJson("breaches" + (domain != null ? "?domain=" + domain : ""));

        List<Breach> breaches = new ArrayList<>();

        for (Object jsonArray : jsonResponse.getBody().getArray()) {
            String jsonArrayString = jsonArray.toString();
            breaches.add(gson.fromJson(jsonArrayString, Breach.class));
        }

        if (domain == null) {
            hibpCache.setCachedBreaches(breaches);
        } else {
            hibpCache.addDomain(domain, breaches);
        }

        return breaches;
    }

    public List<Breach> getUserBreaches(String userID) throws ApiException {
        if (userID == null) {
            throw new InvalidAPIRequestException();
        }

        if (hibpCache.getUserCache(userID) != null) {
            return hibpCache.getUserCache(userID);
        }

        HttpResponse<JsonNode> jsonResponse = getJson("breachedaccount/" + userID);

        List<Breach> breaches = new ArrayList<>();

        for (Object jsonArray : jsonResponse.getBody().getArray()) {
            String jsonArrayString = jsonArray.toString();
            breaches.add(gson.fromJson(jsonArrayString, Breach.class));
        }

        hibpCache.addUser(userID, breaches);

        return breaches;
    }

    /**
     * Use's Unirest to return a HttpResponse following the specified parameters
     *
     * @param params String specified parameters, which are appended onto the baseUrl
     *
     * @return HttpResponse the response from Unirest
     */
    private HttpResponse<JsonNode> getJson(String params) throws ApiException {
        if (params == null || params.isEmpty()) {
            throw new InvalidAPIRequestException();
        }

        Unirest.setHttpClient(makeClient());

        try {
            return Unirest.get(baseUrl + params).headers(headers).asJson();
        } catch (UnirestException e) {
            throw new ApiUnirestException(e);
        }
    }
}
