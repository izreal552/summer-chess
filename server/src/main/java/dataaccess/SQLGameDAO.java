package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

public class SQLGameDAO implements GameDAO {
    public SQLGameDAO() {
        initializeDatabase();
    }

    private static final Gson gson = new Gson();

    @Override
    public HashSet<GameData> listGames() throws DataAccessException {
        HashSet<GameData> games = new HashSet<>();

        String query = "SELECT gameID, whiteUsername, blackUsername, gameName, chessGame FROM game";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet results = stmt.executeQuery()) {

            while (results.next()) {
                games.add(extractGameData(results));
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
        String insert = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insert)) {

            stmt.setInt(1, gameID);
            stmt.setString(2, whiteUsername);
            stmt.setString(3, blackUsername);
            stmt.setString(4, gameName);
            stmt.setString(5, serializeGame(game));
            stmt.executeUpdate();

        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to create game: " + e.getMessage(), e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String query = "SELECT whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, gameID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GameData(
                            gameID,
                            rs.getString("whiteUsername"),
                            rs.getString("blackUsername"),
                            rs.getString("gameName"),
                            deserializeGame(rs.getString("chessGame"))
                    );
                } else {
                    throw new DataAccessException("Game not found with ID: " + gameID);
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to fetch game: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean gameExists(int gameID) {
        String query = "SELECT gameID FROM game WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, gameID);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException | DataAccessException e) {
            return false;
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String update = "UPDATE game SET whiteUsername = ?, blackUsername = ?, gameName = ?, chessGame = ? WHERE gameID = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(update)) {

            stmt.setString(1, game.whiteUsername());
            stmt.setString(2, game.blackUsername());
            stmt.setString(3, game.gameName());
            stmt.setString(4, serializeGame(game.game()));
            stmt.setInt(5, game.gameID());

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new DataAccessException("No game found to update with ID: " + game.gameID());
            }

        } catch (SQLException e) {
            throw new DataAccessException("Failed to update game: " + e.getMessage(), e);
        }
    }

    @Override
    public void clear() {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("TRUNCATE game")) {
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException exception) {
            throw new RuntimeException("Failed to clear game table", exception);
        }
    }

    private GameData extractGameData(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        ChessGame game = deserializeGame(rs.getString("chessGame"));
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    private String serializeGame(ChessGame game) {
        return gson.toJson(game);
    }

    private ChessGame deserializeGame(String json) {
        return gson.fromJson(json, ChessGame.class);
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



    private void initializeDatabase() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to create database", e);
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREATE_TABLE_SQL)) {
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to create game table", e);
        }
    }
}
