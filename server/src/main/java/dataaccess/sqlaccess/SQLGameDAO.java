package dataaccess.sqlaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.GameDAO;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO {
    @Override
    public int createGame(GameData gameData) throws DataAccessException {
        var statement = "INSERT INTO games (whiteusername, blackusername, gamename, game) VALUES ( ?, ?, ?, ?)";
        String game = new Gson().toJson(gameData.game());
        return ExecuteUpdate.executeUpdate(statement, gameData.whiteUsername(),
                gameData.blackUsername(), gameData.gameName(), game);
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE gameid=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String game = rs.getString("game");
                        ChessGame chessGame = new Gson().fromJson(game, ChessGame.class);
                        return new GameData(rs.getInt("gameid"), rs.getString("whiteusername"),
                                rs.getString("blackusername"), rs.getString("gamename"),
                                chessGame);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void clear() throws DataAccessException{
        var statement = "TRUNCATE games";
        ExecuteUpdate.executeUpdate(statement);
    }
}
