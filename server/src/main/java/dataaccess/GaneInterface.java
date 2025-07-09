package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashSet;

public interface GaneInterface {
    HashSet<GameData> listGames()       throws DataAccessException;
    void createGame(GameData game)      throws DataAccessException;
    void createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game);
    GameData getGame(int gameID)        throws DataAccessException;
    boolean gameExists(int gameID);
    void updateGame(GameData game)      throws DataAccessException;
    void clear();
}
