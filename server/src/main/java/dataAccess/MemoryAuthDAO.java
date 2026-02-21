package dataAccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private HashMap<String, AuthData> authTable = new HashMap<>();

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        if(authTable.containsKey(authData.authToken())){
            throw new DataAccessException("AuthToken already exists");
        }
        else{
            authTable.put(authData.authToken(), authData);
        }
    }

    @Override
    public AuthData getAuth(String authToken){
        if(authTable.containsKey(authToken)){
            return authTable.get(authToken);
        }
        else{
            return null;
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        if(!authTable.containsKey(authToken)){
            throw new DataAccessException("AuthToken does not exist");
        }
        else{
            authTable.remove(authToken);
        }
    }

    @Override
    public void clear(){
        authTable.clear();
    }
}
