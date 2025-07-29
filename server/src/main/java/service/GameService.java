package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public HashSet<GameData> listGames(String authToken) throws DataAccessException {
        ensureAuthorized(authToken);
        return gameDAO.listGames();
    }

    public GameData getGame(String authToken, int gameID) throws DataAccessException {
        ensureAuthorized(authToken);

        try {
            return gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new BadRequestException("Invalid game ID: " + e.getMessage());
        }
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        authDAO.getAuth(authToken);

        // Get all existing games
        Collection<GameData> allGames = gameDAO.listGames();

        int maxId = 0;
        for (GameData game : allGames) {
            if (game.gameID() > maxId) {
                maxId = game.gameID();
            }
        }

        int gameID = maxId + 1;

        GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());
        gameDAO.createGame(newGame);

        return gameID;

    }

    public boolean joinGame(String authToken, int gameID, String color) throws DataAccessException {
        AuthData authData;
        GameData gameData;

        try {
            authData = authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("Auth")) {
                throw new UnauthorizedException("auth");
            }
            throw new UnauthorizedException("invalid");
        }

        try {
            gameData = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }

        String whiteUser = gameData.whiteUsername();
        String blackUser = gameData.blackUsername();
        String username = authData.username();

        if (Objects.equals(color, "WHITE")) {
            if (whiteUser != null) {
                return false;
            }
            whiteUser = username;
        } else if (Objects.equals(color, "BLACK")) {
            if (blackUser != null) {
                return false;
            }
            blackUser = username;
        } else {
            throw new BadRequestException("%s is not a valid team color".formatted(color));
        }

        try {
            GameData updatedGame = new GameData(gameID, whiteUser, blackUser, gameData.gameName(), gameData.game());
            gameDAO.updateGame(updatedGame);
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }

        return true;
    }


    public void clear() {
        gameDAO.clear();
        authDAO.clear();
    }

    private void ensureAuthorized(String authToken) throws DataAccessException {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("failed")) {
                throw new UnauthorizedException("failed");
            }
            throw new UnauthorizedException("invalid");
        }
    }

}
