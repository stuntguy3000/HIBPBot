package me.stuntguy3000.java.telegram.hibpbot.object;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Pattern;

/**
 * @author stuntguy3000
 */
public class Util {
    private static Pattern URL_REGEX = Pattern.compile("^(?:(?!-)[\\w-]{1,63}(?<!-)\\.)+(?!-)[\\w-]{1,63}(?<!-)$", Pattern.CASE_INSENSITIVE);
    private static Pattern USER_REGEX = Pattern.compile("^[a-zA-Z0-9.]+$|^[a-zA-Z0-9.]+@([a-zA-Z0-9]+\\.)+[a-zA-Z.]+$", Pattern.CASE_INSENSITIVE);
    /**
     * Returns if the input matches the regex validation
     *
     * @param input String the string to be tested
     *
     * @return true if the input is valid
     */
    public static boolean isValidURL(String input) {
        return URL_REGEX.matcher(input).find();
    }

    /**
     * Returns if the input matches the regex validation
     *
     * @param input String the string to be tested
     *
     * @return true if the input is valid
     */
    public static boolean isValidUsername(String input) {
        return USER_REGEX.matcher(input).find();
    }

    public static String plural(String text, int size) {
        if (size != 0) {
            return text;
        } else {
            return "";
        }
    }

    public static String getText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        return response.toString();
    }
}
