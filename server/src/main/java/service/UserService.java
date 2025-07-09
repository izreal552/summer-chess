package service;


import dataaccess.DataAccessException;
import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData createUser(UserData userData) throws DataAccessException {
        validateUserData(userData);

        try {
            userDAO.createUser(userData);
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }

        String authToken = generateAuthToken();
        AuthData auth = new AuthData(userData.username(), authToken);
        authDAO.addAuth(auth);
        return auth;
    }

    public AuthData loginUser(UserData userData) throws DataAccessException{
        validateUserData(userData);

        boolean authenticated = userDAO.authUser(userData.username(), userData.password());
        if (!authenticated) {
            throw new DataAccessException("Invalid credentials");
        }

        String authToken = generateAuthToken();
        authDAO.addAuth(authToken, userData.username());
        return new AuthData(userData.username(), authToken);
    }

    public void logoutUser(String authToken) throws DataAccessException {
        if (authToken == null || authToken.isBlank()) {
            throw new DataAccessException("Missing authorization token");
        }

        try {
            authDAO.getAuth(authToken);
            authDAO.delAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("Database operation failed: " + e.getMessage());
        }
    }

    public void clear(){
        userDAO.clear();
        authDAO.clear();
    }

    private void validateUserData(UserData userData) throws BadRequestException {
        if (userData == null || userData.username() == null || userData.password() == null) {
            throw new BadRequestException("Missing required fields");
        }
    }

    private String generateAuthToken() {
        return UUID.randomUUID().toString();
    }
}
