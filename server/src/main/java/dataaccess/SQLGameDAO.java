package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.HashSet;

public class SQLGameDAO implements GameDAO {
    public SQLGameDAO(){
        initializeDatabase();
    }

    private static final Gson GSON = new Gson();

    @Override
    public HashSet<GameData> listGames() throws DataAccessException {
        var games = new HashSet<GameData>();
        var stmt = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(stmt)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        games.add(extractGameData(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to list games: " + e.getMessage(), e);
        }
        return games;
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        createGame(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    }

    @Override
    public void createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
        String stmt = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(stmt)) {
                ps.setInt(1, gameID);
                ps.setString(2, whiteUsername);
                ps.setString(3, blackUsername);
                ps.setString(4, gameName);
                ps.setString(5, GSON.toJson(game)); //serialize game
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to create game: " + e.getMessage(), e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        var stmt = "SELECT whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(stmt)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    rs.next();
                    var whiteUsername = rs.getString("whiteUsername");
                    var blackUsername = rs.getString("blackUsername");
                    var gameName = rs.getString("gameName");
                    var chessGame = GSON.fromJson(rs.getString("chessGame"), ChessGame.class); //deserialize game
                    return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                }
            }
        } catch (SQLException error) {
            throw new DataAccessException("Game not found, id: " + gameID);
        }
    }

    @Override
    public boolean gameExists(int gameID) {
        var stmt = "SELECT gameID FROM game WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(stmt)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException | DataAccessException e) {
            return false;
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        var stmt = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, chessGame = ? WHERE gameID = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(stmt)) {
                ps.setString(1, game.whiteUsername());
                ps.setString(2, game.blackUsername());
                ps.setString(3, game.gameName());
                ps.setString(4, GSON.toJson(game.game())); // Serialize
                ps.setInt(5, game.gameID());

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new DataAccessException("No game found to update with ID: " + game.gameID());
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to update game: " + e.getMessage(), e);
        }
    }

    @Override
    public void clear() {
        var stmt = "TRUNCATE game";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(stmt)) {
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to clear game table", e);
        }
    }

    private GameData extractGameData(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        ChessGame game = GSON.fromJson(rs.getString("chessGame"), ChessGame.class); //deserialize game
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    private static final String CREATE_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS game (
            gameID INT NOT NULL,
            whiteUsername VARCHAR(255),
            blackUsername VARCHAR(255),
            gameName VARCHAR(255),
            chessGame TEXT,
            PRIMARY KEY (gameID)
        )
    """;



    private void initializeDatabase(){
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Failed to initialize database", exception);
        }

        try (var conn = DatabaseManager.getConnection()){
            try (var stmt = conn.prepareStatement(CREATE_TABLE_SQL)) {
                stmt.executeUpdate();
            }
        }catch (SQLException | DataAccessException exception) {
            throw new RuntimeException("Failed to create game table", exception);
        }
    }

}
