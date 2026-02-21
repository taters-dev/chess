package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.HashMap;

import static chess.ChessGame.TeamColor.WHITE;

public class MemoryGameDAO implements GameDAO {
    private HashMap<Integer, GameData> gameTable = new HashMap<>();
    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        GameData realGame = new GameData(gameData.gameID(), gameData.whiteUsername(),
                gameData.blackUsername(), gameData.gameName(), gameData.game());
        if(gameTable.containsKey(realGame.gameID())){
            throw new DataAccessException("Game Already Exists");
        }
        else{
            gameTable.put(realGame.gameID(), realGame);
            return realGame.gameID();
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
    public GameData getGame(int gameID) {
        if(gameTable.containsKey(gameID)){
            return gameTable.get(gameID);
        }
        else{
            return null;
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
