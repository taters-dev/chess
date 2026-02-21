package dataAccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO{
    private HashMap<String, UserData> userTable = new HashMap<>();

    @Override
    public void createUser(UserData userData) throws DataAccessException{
        if(userTable.containsKey(userData.username())){
            throw new DataAccessException("User Already Exists");
        }
        else {
            userTable.put(userData.username(), userData);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        if(userTable.containsKey(username)){
            return userTable.get(username);
        }
        else{
            throw new DataAccessException("User Does not Exist");
        }
    }

    @Override
    public void clear(){
        userTable.clear();
    }
}
