package dataaccess.sqlaccess;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO games (whiteusername, blackusername, gamename, game) VALUES ( ?, ?, ?, ?)";
        String game = new Gson().toJson(gameData.game());
        int gameID = ExecuteUpdate.executeUpdate(statement, gameData.whiteUsername(),
                gameData.blackUsername(), gameData.gameName(), game);
        return gameID;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void clear() {

    }
}
