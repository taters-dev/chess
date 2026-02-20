package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private HashMap<Integer, GameData> gameTable = new HashMap<>();
    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        if(gameTable.containsKey(gameData.gameID())){
            throw new DataAccessException("Game Already Exists");
        }
        else{
            gameTable.put(gameData.gameID(), gameData);
            return gameData.gameID();
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        if(gameTable.containsKey(gameData.gameID())){
            gameTable.replace(gameData.gameID(), gameData);
        }
        else{
            throw new DataAccessException("Game does not exist");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        if(gameTable.containsKey(gameID)){
            return gameTable.get(gameID);
        }
        else{
            throw new DataAccessException("Game does not exist");
        }
    }

    @Override
    public Collection<GameData> listGames(){
        return gameTable.values();
    }

    @Override
    public void clear(){
        gameTable.clear();
    }
}
