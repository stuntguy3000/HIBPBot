package me.stuntguy3000.java.telegram.hibpbot.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Handles the automatic updating of jar files from a Jenkins CI instance
 * <p>Url must end follow the following format: http://<domain>/job/</p>
 * TODO: Regex validation?
 *
 * @author stuntguy3000
 */
@Data
public class JenkinsUpdateHandler {
    private Gson gson;
    private String jenkinsID;
    private String jenkinsBaseUrl;
    private String jenkinsFileName;
    private File updateInformationFile;
    private UpdateInformation lastUpdate;
    private JenkinsUpdateThread updateThread;
    private long delay;
    private Timer timer;

    /**
     * Initiate a new JenkinsUpdateHandler instance
     * <p>jenkinsBaseUrl must end follow the following format: http://<domain>/job/</p>
     *
     * @param jenkinsID       String the jenkins job ID
     * @param jenkinsBaseUrl  String the jenkins base URL
     * @param jenkinsFileName String the name of the file to be downloaded (incl. extension)
     * @param delay           the delay in ms between update checks
     */
    public JenkinsUpdateHandler(String jenkinsID, String jenkinsBaseUrl, String jenkinsFileName, long delay) {
        // Init variables
        setJenkinsID(jenkinsID);
        setJenkinsBaseUrl(jenkinsBaseUrl);
        setJenkinsFileName(jenkinsFileName);
        setDelay(delay);

        // Init Classes
        gson = new GsonBuilder().create();
        timer = new Timer();

        // Load existing update information
        try {
            loadUpdateInformation();
        } catch (JenkinsUpdateException ex) {
            System.err.println("JenkinsUpdateException has occurred.");

            if (ex.getException() != null) {
                System.err.println("Printing error stacktrace...");
                ex.getException().printStackTrace();
            } else {
                System.err.println(ex.getMessage());
            }
        }
    }

    /**
     * Load update information from file
     */
    private void loadUpdateInformation() throws JenkinsUpdateException {
        try {
            if (updateInformationFile == null) {
                updateInformationFile = new File("update");
            }

            if (updateInformationFile.exists()) {
                lastUpdate = gson.fromJson(new BufferedReader(new FileReader(updateInformationFile)), UpdateInformation.class);
            } else {
                if (updateInformationFile.createNewFile()) {
                    lastUpdate = new UpdateInformation();
                    saveNewUpdateInformation(lastUpdate);
                } else {
                    throw new JenkinsUpdateException("Unable to create new update file.");
                }
            }

        } catch (IOException ex) {
            throw new JenkinsUpdateException(ex);
        }
    }

    /**
     * Start the auto updater's thread. Throws an exception if the thread is already running.
     */
    public void startUpdater() throws JenkinsUpdateException {
        if (updateThread != null) {
            throw new JenkinsUpdateException("Updater already running");
        }

        updateThread = new JenkinsUpdateThread(this);
        timer.schedule(updateThread, delay, delay);
    }

    /**
     * Returns an UpdateInformation instance for the build
     * <p>Inspired from https://raw.githubusercontent.com/nickrobson/SuperBot/master/src/main/java/xyz/nickr/superbot/SuperBotController.java</p>
     *
     * @param build int the number of the Jenkins build
     *
     * @return UpdateInformation the information for this build
     */
    private UpdateInformation getUpdateInformation(int build) throws JenkinsUpdateException {
        try {
            String url = getUrl("/" + build + "/api/json?tree=changeSet[items[id,msg,author[id]]]");
            HttpResponse<String> unirestRequest = Unirest.get(url).asString();

            if (unirestRequest.getStatus() == 200) {
                JsonObject obj = gson.fromJson(unirestRequest.getBody(), JsonObject.class);
                JsonArray details = obj.getAsJsonObject("changeSet").getAsJsonArray("items");

                LinkedList<String> gitCommitIds = new LinkedList<>();
                LinkedList<String> gitCommitMessages = new LinkedList<>();
                LinkedList<String> gitCommitAuthors = new LinkedList<>();

                for (int i = 0; i < details.size(); i++) {
                    gitCommitIds.add(details.get(i).getAsJsonObject().get("id").getAsString().trim());
                    gitCommitMessages.add(details.get(i).getAsJsonObject().get("msg").getAsString().trim());
                    gitCommitAuthors.add(details.get(i).getAsJsonObject().getAsJsonObject("author").get("id").getAsString().trim());
                }
                return new UpdateInformation(gitCommitIds, gitCommitMessages, gitCommitAuthors, build);

            } else {
                throw new JenkinsUpdateException("Jenkins returned a status code of " + unirestRequest.getStatus());
            }
        } catch (Exception ex) {
            throw new JenkinsUpdateException(ex);
        }
    }

    /**
     * Creates a String, combining the base url and the jenkins ID
     *
     * @param params String any extra parameters
     *
     * @return String the created url
     */
    String getUrl(String params) throws JenkinsUpdateException {
        if (params == null) {
            params = "";
        }

        return getJenkinsBaseUrl().concat(getJenkinsID()).concat(params);
    }

    /**
     * Save new Update Information to file
     *
     * @param updateInformation UpdateInformation the information to save
     */
    private void saveNewUpdateInformation(UpdateInformation updateInformation) throws JenkinsUpdateException {
        try {
            if (!updateInformationFile.exists()) {
                if (!updateInformationFile.createNewFile()) {
                    throw new JenkinsUpdateException("Unable to create new update file.");
                }
            }

            String outputText = gson.toJson(updateInformation);

            FileOutputStream outputStream = new FileOutputStream(updateInformationFile);
            outputStream.write(outputText.getBytes());
            outputStream.close();
        } catch (IOException e) {
            throw new JenkinsUpdateException(e);
        }
    }


    /**
     * Represents a JSON file containing update information
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public class UpdateInformation {
        private LinkedList<String> gitCommitIds;
        private LinkedList<String> gitCommitMessages;
        private LinkedList<String> gitCommitAuthors;
        private int buildNumber;
    }

    /**
     * Represents a throwable exception
     */
    @EqualsAndHashCode(callSuper = true)
    @Data
    public class JenkinsUpdateException extends Throwable {
        private Exception exception;
        private String message;

        /**
         * Creates a new JenkinsUpdateException with an exception
         *
         * @param exception Exception the exception which was thrown
         */
        JenkinsUpdateException(Exception exception) {
            this.exception = exception;
        }

        /**
         * Creates a new JenkinsUpdateException with a message
         *
         * @param message String the message which is being thrown
         */
        JenkinsUpdateException(String message) {
            this.message = message;
        }
    }


    /**
     * Represents the updater thread
     */
    @EqualsAndHashCode(callSuper = true)
    @AllArgsConstructor
    @Data
    private class JenkinsUpdateThread extends TimerTask {
        private JenkinsUpdateHandler updateHandler;

        @Override
        public void run() {
            try {
                int newJenkinsBuildNumber = 0;
                if (updateHandler.getLastUpdate() != null) {
                    /**
                     * Check the latest build on Jenkins against the known last build
                     */
                    HttpResponse<String> unirestRequest;

                    try {
                        unirestRequest = Unirest.get(updateHandler.getUrl("/lastSuccessfulBuild/buildNumber")).asString();
                    } catch (UnirestException e) {
                        throw new JenkinsUpdateException(e);
                    }

                    if (unirestRequest.getStatus() == 200) {
                        newJenkinsBuildNumber = Integer.parseInt(unirestRequest.getBody());
                    } else {
                        throw new JenkinsUpdateException("Jenkins returned a status code of " + unirestRequest.getStatus());
                    }
                }

                /**
                 * Validation check
                 */
                if (newJenkinsBuildNumber > updateHandler.getLastUpdate().getBuildNumber()) {
                    downloadUpdate(newJenkinsBuildNumber);
                    System.exit(0);
                }

            } catch (JenkinsUpdateException ex) {
                System.err.println("JenkinsUpdateException has occurred.");

                if (ex.getException() != null) {
                    System.err.println("Printing error stacktrace...");
                    ex.getException().printStackTrace();
                } else {
                    System.err.println(ex.getMessage());
                }
            }
        }

        /**
         * Download the newest update
         *
         * @param newBuildNumber int new build number
         */
        private void downloadUpdate(int newBuildNumber) throws JenkinsUpdateException {
            try {
                JenkinsUpdateHandler.UpdateInformation updateInformation = updateHandler.getUpdateInformation(newBuildNumber);
                updateHandler.saveNewUpdateInformation(updateInformation);

                BufferedInputStream in = new BufferedInputStream(
                        new URL(updateHandler.getUrl("/lastSuccessfulBuild/artifact/target/" + updateHandler.getJenkinsFileName()))
                                .openStream()
                );

                FileOutputStream fos = new FileOutputStream(
                        updateHandler.getJenkinsFileName().concat(".new")
                );

                BufferedOutputStream bout = new BufferedOutputStream(fos);

                byte data[] = new byte[1024];
                int read;
                while ((read = in.read(data, 0, 1024)) >= 0) {
                    bout.write(data, 0, read);
                }

                bout.close();
                in.close();
            } catch (IOException ex) {
                throw new JenkinsUpdateException(ex);
            }
        }
    }
}

