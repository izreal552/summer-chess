package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {

    private GameService gameService;
    private UserService userService;
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private String token;


    @BeforeEach
    public void setUp(){
        userDAO = new MemoryUserDAO();
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
    }

    @Test
    public void testListGamesPositive() throws DataAccessException {
        UserData user = new UserData("gamer1", "pass", "email");
        AuthData auth = userService.createUser(user);
        HashSet<GameData> games = gameService.listGames(auth.authToken());
        assertNotNull(games);
    }

    @Test
    public void testListGamesNegative() {
        assertThrows(UnauthorizedException.class, () -> gameService.listGames("bad_token"));
    }

    @Test
    public void testCreateGamePositive() throws DataAccessException {
        UserData user = new UserData("gamer2", "pass", "email");
        AuthData auth = userService.createUser(user);
        int gameId = gameService.createGame(auth.authToken(), "chess");
        assertTrue(gameId > 0);
    }

    @Test
    public void testCreateGameNegative() {
        assertThrows(DataAccessException.class, () -> gameService.createGame("invalid", "chess"));
    }

    @Test
    public void testGetGamePositive() throws DataAccessException {
        UserData user = new UserData("gamer3", "pass", "email");
        AuthData auth = userService.createUser(user);
        int gameId = gameService.createGame(auth.authToken(), "checkers");
        GameData game = gameService.getGame(auth.authToken(), gameId);
        assertEquals("checkers", game.gameName());
    }

    @Test
    public void testGetGameNegative() throws DataAccessException {
        UserData user = new UserData("gamer4", "pass", "email");
        AuthData auth = userService.createUser(user);
        assertThrows(BadRequestException.class, () -> gameService.getGame(auth.authToken(), 9999));
    }

    @Test
    void joinGamePositive() throws DataAccessException {
        UserData user = new UserData("joinUser", "password", "email");
        AuthData auth = userService.createUser(user);
        String token = auth.authToken();
        int gameID = gameService.createGame(token, "Join Game");
        assertDoesNotThrow(() -> gameService.joinGame(token, gameID, "WHITE"));
    }

    @Test
    void joinGameNegative() {
        assertThrows(UnauthorizedException.class, () -> gameService.joinGame("bad-token",
                99, "BLACK"));
    }
    
    @Test
    public void testClearPositive() throws DataAccessException {
        userDAO.clear();
        gameDAO.clear();
        authDAO.clear();
        assertTrue(gameDAO.listGames().isEmpty());
    }
}
