package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData createUser(UserData userData) throws DataAccessException;
    UserData getUser(UserData userData) throws DataAccessException;
    void clear() throws DataAccessException;
}
