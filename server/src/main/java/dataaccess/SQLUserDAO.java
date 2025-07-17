package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {
    public SQLUserDAO(){
        initializeDatabase();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        createUser(user.username(), user.password(), user.email());
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        String query = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection(); var stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("User already exists: " + username);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var stmt = "SELECT username, password, email FROM user WHERE username = ?";
            try (var ps = conn.prepareStatement(stmt)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        return new UserData(username, password, email);
                    } else {
                        throw new DataAccessException("User not found: " + username);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving user: " + e.getMessage());
        }
    }

    @Override
    public boolean authUser(String username, String password) throws DataAccessException {
        UserData user = getUser(username);
        return BCrypt.checkpw(password, user.password());
    }

    @Override
    public void clear() {
        var truncate = "TRUNCATE user";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(truncate)) {
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to clear user table", e);
        }
    }

    private final String[] tableStatement = {"""
        CREATE TABLE IF NOT EXISTS user (
            username VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255),
            PRIMARY KEY (username)
        )
    """};

    private void initializeDatabase(){
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException exception) {
            throw new RuntimeException("Failed to initialize database", exception);
        }

        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : tableStatement) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        }catch (SQLException | DataAccessException exception) {
            throw new RuntimeException("Failed to create user table", exception);
        }
    }
}
