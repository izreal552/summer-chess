package dataaccess;

import model.AuthData;

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
        String stmt = "SELECT username, authToken FROM auth WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(stmt)) {
                ps.setString(1, authToken);

                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String username = rs.getString("username");
                        return new AuthData(username, authToken);
                    } else {
                        throw new UnauthorizedException("Auth token does not exist: " + authToken);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get authToken: " + e.getMessage());
        }
    }

    @Override
    public void clear() {
        var stmt = "TRUNCATE auth";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(stmt)) {
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to clear auth table", e);
        }
    }

    private final String[] CREATE_TABLE_SQL = {"""
        CREATE TABLE IF NOT EXISTS auth (
            username VARCHAR(255) NOT NULL,
            authToken VARCHAR(255) NOT NULL,
            PRIMARY KEY (authToken)
        )
    """};

    private void initializeDatabase() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Failed to initialize database", exception);
        }

        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : CREATE_TABLE_SQL) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }catch (SQLException | DataAccessException exception) {
            throw new RuntimeException("Failed to create auth table", exception);
        }
    }
}
