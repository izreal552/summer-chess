package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    @BeforeEach
    public void setUp(){
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();

        userService = new UserService(userDAO, authDAO);

        userDAO.clear();
        authDAO.clear();
    }

    @Test
    void createUserPositive() throws DataAccessException {
        UserData user = new UserData("user1", "pass", "email");
        AuthData auth = userService.createUser(user);
        assertNotNull(auth.authToken());
    }

    @Test
    void createUserNegative() {
        UserData user = new UserData("user1", "pass", "email");
        assertDoesNotThrow(() -> userService.createUser(user));
        assertThrows(BadRequestException.class, () -> userService.createUser(user));
    }

    @Test
    void loginUserPositive() throws DataAccessException {
        UserData user = new UserData("user2", "pass", "email");
        userService.createUser(user);
        AuthData auth = userService.loginUser(user);
        assertNotNull(auth);
    }

    @Test
    void loginUserNegative() {
        UserData user = new UserData("user3", "wrong", "email");
        assertThrows(DataAccessException.class, () -> userService.loginUser(user));
    }

    @Test
    void logoutUserPositive() throws DataAccessException {
        UserData user = new UserData("user4", "pass", "email");
        AuthData auth = userService.createUser(user);
        assertDoesNotThrow(() -> userService.logoutUser(auth.authToken()));
    }
    
    @Test
    void logoutUserNegative() {
        assertThrows(UnauthorizedException.class, () -> userService.logoutUser("fake-token"));
    }

    @Test
    void clearPositive() {
        assertDoesNotThrow(() -> userService.clear());
    }
}
