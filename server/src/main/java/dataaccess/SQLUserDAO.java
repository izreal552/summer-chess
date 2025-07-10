package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO {
    public SQLUserDAO() {
        initializeDatabase();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        createUser(user.username(), user.password(), user.email());
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        String hashedPassword = hashPassword(password);

        String insertSQL = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DataAccessException("Username already taken");
            }
            throw new DataAccessException("Failed to create user: " + e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        String query = "SELECT username, password, email FROM user WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String password = rs.getString("password");
                    String email = rs.getString("email");
                    return new UserData(username, password, email);
                } else {
                    throw new DataAccessException("User not found: " + username);
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving user: " + e.getMessage());
        }
    }

    @Override
    public boolean authUser(String username, String password) throws DataAccessException {
        UserData user = getUser(username);
        return passwordMatching(password, user.password());
    }

    @Override
    public void clear() {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("TRUNCATE user")) {
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to clear user table", e);
        }
    }

    private static final String CREATE_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS user (
            username VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255),
            PRIMARY KEY (username)
        )
    """;

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean passwordMatching(String rawPassword, String hashedPassword) {
        return BCrypt.checkpw(rawPassword, hashedPassword);
    }

    private void initializeDatabase() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            throw new RuntimeException("Failed to initialize database", ex);
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CREATE_TABLE_SQL)) {
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to create user table", e);
        }
    }
}
