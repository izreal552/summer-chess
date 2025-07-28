import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;

public class ServerFacade {

    private String serverUrl = "http://localhost:8080";
    private String authToken;
    private final Gson gson = new Gson();

    public void setServerPort(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

    // Helper functions
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
