import com.google.gson.Gson;
import model.GameData;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;

public class ServerFacade {

    private String serverUrl = "http://localhost:8080";
    private String authToken;
    private final Gson gson = new Gson();

    public void setServerPort(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    // user management
    public boolean register(String username, String password, String email) {
        var path = "/user";
        var body = Map.of("username", username, "password", password, "email", email);

        Map<String, Object> response = makeRequest("POST", path, body);
        if (response.containsKey("Error")) {
            return false;
        }
        authToken = (String) response.get("authToken");
        return true;
    }

    public boolean login(String username, String password) {
        var path = "/session";
        var body = Map.of("username", username, "password", password);

        Map<String, Object> response = makeRequest("POST", path, body);
        if (response.containsKey("Error")) {
            return false;
        }
        authToken = (String) response.get("authToken");
        return true;
    }

    public boolean logout() {
        var path = "/session";
        Map<String, Object> response = makeRequest("DELETE", path, null);

        if (response.containsKey("Error")) {
            return false;
        }
        authToken = null;
        return true;
    }







    private Map<String, Object> makeRequest(String method, String path, Object request) {
        try {
            HttpURLConnection http = setupConnection(method, path, request);

            int status = http.getResponseCode();
            if (status == 401) {
                return Map.of("Error", "Unauthorized (401)");
            }
            if (!isSuccessful(status)) {
                return Map.of("Error", "HTTP " + status + " - " + readError(http));
            }

            try (InputStream respBody = http.getInputStream();
                 InputStreamReader reader = new InputStreamReader(respBody)) {
                return gson.fromJson(reader, Map.class);
            }

        } catch (Exception e) {
            return Map.of("Error", e.getMessage());
        }
    }

    private String makeRequestRaw(String method, String path, Object request) {
        try {
            HttpURLConnection http = setupConnection(method, path, request);
            int status = http.getResponseCode();
            if (status == 401) {
                return "Error: Unauthorized (401)";
            }
            if (!isSuccessful(status)) {
                return "Error: HTTP " + status + " - " + readError(http);
            }
            try (InputStream respBody = http.getInputStream()) {
                return readString(new InputStreamReader(respBody));
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }


    // Helper functions
    private HttpURLConnection setupConnection(String method, String path, Object request) throws Exception {
        URL url = (new URI(serverUrl + path)).toURL();
        HttpURLConnection http = (HttpURLConnection) url.openConnection();
        http.setRequestMethod(method);
        if (authToken != null) {
            http.addRequestProperty("authorization", authToken);
        }
        if (request != null) {
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            String jsonData = gson.toJson(request);
            try (OutputStream os = http.getOutputStream()) {
                os.write(jsonData.getBytes());
            }
        }
        http.connect();
        return http;
    }

    private String readString(InputStreamReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int character; (character = reader.read()) != -1; ) {
            sb.append((char) character);
        }
        return sb.toString();
    }

    private String readError(HttpURLConnection http) {
        try (InputStream errStream = http.getErrorStream()) {
            if (errStream == null) {
                return "No error body";
            }
            return readString(new InputStreamReader(errStream));
        } catch (IOException e) {
            return "Failed to read error stream";
        }
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
