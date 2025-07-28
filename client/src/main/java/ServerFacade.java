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

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
