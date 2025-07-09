package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {

    private Map<String, UserData> db;

    public MemoryUserDAO() {
        db = new HashMap<>();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        UserData user = db.get(username);
        if (user == null) {
            throw new DataAccessException("User: " + username + " not found");
        }
        return user;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (db.containsKey(user.username())) {
            throw new DataAccessException("User: " + user.username() + " already exists");
        }
        db.put(user.username(), user);
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        if (db.containsKey(username)) {
            throw new DataAccessException("User already exists: " + username);
        }
        db.put(username, new UserData(username, password, email));
    }

    @Override
    public boolean authUser(String username, String password) throws DataAccessException {
        UserData user = db.get(username);
        if (user == null) {
            throw new DataAccessException("User: " + username + " does not exist");
        }
        return user.password().equals(password);
    }

    @Override
    public void clear() {
        db.clear();
    }
}
