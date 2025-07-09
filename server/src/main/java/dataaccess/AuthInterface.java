package dataaccess;

import model.AuthData;

public interface AuthInterface {
    void addAuth(AuthData authData)                 throws  DataAccessException;
    void addAuth(String authToken, String username) throws DataAccessException;
    void delAuth(String authToken)                  throws DataAccessException;
    AuthData getAuth(String authToken)              throws DataAccessException;
    void clear();
}
