package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {

    private Map<String, AuthData> db;

    public MemoryAuthDAO() {
        db = new HashMap<>();
    }

    @Override
    public void addAuth(AuthData authData) {
        if (authData != null) {
            db.put(authData.authToken(), authData);
        }
    }

    @Override
    public void addAuth(String authToken, String username) {
        if (authToken != null && username != null) {
            db.put(authToken, new AuthData(username, authToken));
        }
    }

    @Override
    public void delAuth(String authToken) {
        db.remove(authToken);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        AuthData authData = db.get(authToken);
        if (authData == null) {
            throw new DataAccessException("Auth Token '" + authToken + "' does not exist");
        }
        return authData;
    }

    @Override
    public void clear() {
        db.clear();
    }
}
