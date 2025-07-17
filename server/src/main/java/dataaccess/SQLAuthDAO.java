package dataaccess;

import model.AuthData;

import javax.xml.crypto.Data;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{
    public SQLAuthDAO() {
        initializeDatabase();
    }


    @Override
    public void addAuth(AuthData authData) throws DataAccessException {
        addAuth(authData.authToken(), authData.username());
    }

    @Override
    public void addAuth(String authToken, String username) throws DataAccessException {
        String stmt = "INSERT INTO auth (username, authToken) VALUES(?, ?)";
        try (var conn = DatabaseManager.getConnection()){
            try (var ps = conn.prepareStatement(stmt)){
                ps.setString(1, username);
                ps.setString(2, authToken);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Auth token already exists");
            }
            throw new DataAccessException("Database connection failed: " + e.getMessage());
        }

    }

    @Override
    public void delAuth(String authToken) throws DataAccessException {
        String stmt = "DELETE FROM auth WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(stmt)) {
                ps.setString(1, authToken);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("Auth token not found");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to delete auth: " + e.getMessage());
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement("TRUNCATE auth")) {
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException exception) {
            throw new RuntimeException("Failed to clear auth table", exception);
        }
    }

    private static final String CREATE_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS user (
            username VARCHAR(255) NOT NULL,
            authToken VARCHAR(255) NOT NULL,
            PRIMARY KEY (authToken)
        )
    """;

    private void initializeDatabase() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to create database", e);
        }

        try (var conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(CREATE_TABLE_SQL)) {
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to create game table", e);
        }
    }
}
