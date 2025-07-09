package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUser(String username)                               throws DataAccessException;
    void createUser(UserData user)                                  throws DataAccessException;
    void createUser(String username, String password, String email) throws DataAccessException;
    boolean authUser(String username, String password)              throws DataAccessException;
    void clear();
}
