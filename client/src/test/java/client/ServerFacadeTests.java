package client;

import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static final String HOST = "localhost";
    private static int port;
    private static String serverUrl;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        serverUrl = "http://" + HOST + ":" + port;
        serverFacade = new ServerFacade(serverUrl);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void setup() {
        server.clearDB();

        ServerFacade facade = new ServerFacade(serverUrl);

        System.out.println("Testing Server on Port:" + port);
    }

    @AfterEach
    void cleanup() {
        server.clearDB();
    }

    @Test
    @Order(1)
    @DisplayName("Register Success")
    public void registerSuccess() {
        var result = serverFacade.register("username", "password", "email");
        assertTrue(result);
    }

    @Test
    @Order(2)
    @DisplayName("Register Duplicate")
    public void registerDuplicate() {
        serverFacade.register("username", "password", "email");
        var result = serverFacade.register("username", "password", "email");
        assertFalse(result);
    }

    @Test
    @Order(3)
    @DisplayName("Login Success")
    public void loginSuccess() {
        serverFacade.register("user", "pass", "email");
        var result = serverFacade.login("user", "pass");
        assertTrue(result);
    }

    @Test
    @Order(4)
    @DisplayName("Login Failure")
    public void loginFailure() {
        serverFacade.register("user", "pass", "email");
        var result = serverFacade.login("user", "wrongpass");
        assertFalse(result);
    }

    @Test
    @Order(5)
    @DisplayName("Logout Success")
    public void logoutSuccess() {
        serverFacade.register("user", "pass", "email");
        var result = serverFacade.logout();
        assertTrue(result);
    }

    @Test
    @Order(6)
    @DisplayName("Logout Failure")
    public void logoutFailure() {
        var result = serverFacade.logout();
        assertFalse(result);
    }

    @Test
    @Order(7)
    @DisplayName("Create Game Success")
    public void createGameSuccess() {
        serverFacade.register("user", "pass", "email");
        int id = serverFacade.createGame("gameName");
        assertTrue(id >= 0);
    }

    @Test
    @Order(8)
    @DisplayName("Create Game Failure (Unauthenticated)")
    public void createGameFailure() {
        int id = serverFacade.createGame("gameName");
        assertEquals(-1, id);
    }

    @Test
    @Order(9)
    @DisplayName("List Games Success")
    public void listGamesSuccess() {
        serverFacade.register("user", "pass", "email");
        serverFacade.createGame("gameName");
        var games = serverFacade.listGames();
        assertEquals(1, games.size());
    }

    @Test
    @Order(10)
    @DisplayName("List Games Failure (Unauthenticated)")
    public void listGamesFailure() {
        var games = serverFacade.listGames();
        assertTrue(games == null || games.isEmpty());
    }

    @Test
    @Order(11)
    @DisplayName("Join Game Success")
    public void joinGameSuccess() {
        serverFacade.register("user", "pass", "email");
        int id = serverFacade.createGame("gameName");
        var result = serverFacade.joinGame(id, "WHITE");
        assertTrue(result);
    }

    @Test
    @Order(12)
    @DisplayName("Join Game Failure (Duplicate Color)")
    public void joinGameFailure() {
        serverFacade.register("user", "pass", "email");
        int id = serverFacade.createGame("gameName");
        serverFacade.joinGame(id, "WHITE");
        var result = serverFacade.joinGame(id, "WHITE");
        assertFalse(result);
    }
}
