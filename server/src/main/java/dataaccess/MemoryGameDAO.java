package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MemoryGameDAO implements GaneInterface {

    private Map<Integer, GameData> db;

    public MemoryGameDAO() {
        db = new HashMap<>(16);
    }

    @Override
    public HashSet<GameData> listGames() {
        return new HashSet<>(db.values());
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        if (game != null) {
            db.put(game.gameID(), game);
        }
    }

    @Override
    public void createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        GameData newGame = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        db.put(gameID, newGame);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        GameData game = db.get(gameID);
        if (game == null) {
            throw new DataAccessException("Game with ID " + gameID + " not found");
        }
        return game;
    }

    @Override
    public boolean gameExists(int gameID) {
        return db.containsKey(gameID);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (game != null) {
            db.put(game.gameID(), game); // Overwrites if exists
        }
    }

    @Override
    public void clear() {
        db.clear();
    }
}
