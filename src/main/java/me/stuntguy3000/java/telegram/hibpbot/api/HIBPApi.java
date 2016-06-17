package me.stuntguy3000.java.telegram.hibpbot.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import me.stuntguy3000.java.telegram.hibpbot.api.exception.ApiException;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.InvalidAPIRequestException;
import me.stuntguy3000.java.telegram.hibpbot.api.exception.NoUserException;
import me.stuntguy3000.java.telegram.hibpbot.api.model.Breach;
import me.stuntguy3000.java.telegram.hibpbot.object.Util;

/**
 * Represents the class used to connect with the HIBP Api
 *
 * @author stuntguy3000
 */
public class HIBPApi {

    private final HIBPCache hibpCache;
    private final String baseUrl = "https://haveibeenpwned.com/api/v2/";
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

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(
                            java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
        }
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
            if (hibpCache.getCachedBreaches() != null && !hibpCache.getCachedBreaches().isEmpty()) {
                return hibpCache.getCachedBreaches();
            }
        } else {
            if (hibpCache.getDomainBreaches(domain) != null) {
                return hibpCache.getDomainBreaches(domain);
            }

            if (!Util.isValidURL(domain)) {
                return null;
            }
        }

        JsonNode jsonResponse = getJson("breaches" + (domain != null ? "?domain=" + domain : ""));

        List<Breach> breaches = new ArrayList<>();

        for (JsonNode jsonArray : jsonResponse) {
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

        if (Util.isValidUsername(userID)) {
            JsonNode jsonResponse = getJson("breachedaccount/" + userID);

            List<Breach> breaches = new ArrayList<>();

            for (JsonNode jsonArray : jsonResponse) {
                String jsonArrayString = jsonArray.toString();
                breaches.add(gson.fromJson(jsonArrayString, Breach.class));
            }

            hibpCache.addUser(userID, breaches);

            return breaches;
        } else {
            throw new InvalidAPIRequestException();
        }
    }

    /**
     * Use's Unirest to return a HttpResponse following the specified parameters
     *
     * @param params String specified parameters, which are appended onto the baseUrl
     *
     * @return HttpResponse the response from Unirest
     */
    private JsonNode getJson(String params) throws ApiException {
        if (params == null || params.isEmpty()) {
            throw new InvalidAPIRequestException();
        }

        try {
            URL url = new URL(baseUrl + params);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setUseCaches(false);

            for (Map.Entry<String, String> header : headers.entrySet()) {
                conn.addRequestProperty(header.getKey(), header.getValue());
            }

            switch (conn.getResponseCode()) {
                case 403:
                case 400: {
                    throw new InvalidAPIRequestException();
                }
                case 404: {
                    throw new NoUserException();
                }
                case 200: {
                    InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                    ObjectMapper mapper = new ObjectMapper();
                    JsonFactory factory = mapper.getFactory();

                    try {
                        JsonParser jsonParser = factory.createParser(reader);
                        return mapper.readTree(jsonParser);
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new InvalidAPIRequestException();
                    }
                }
                default: {
                    throw new ApiException("API returned HTTP code " + conn.getResponseMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(e.getLocalizedMessage());
        }
    }
}
