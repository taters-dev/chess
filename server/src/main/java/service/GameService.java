package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import dataAccess.UnauthorizedException;
import model.GameData;
import requestAndResult.ListGamesRequest;
import requestAndResult.ListGamesResult;

import java.util.Collection;

public class GameService {
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException, UnauthorizedException {
        String authToken = listGamesRequest.authToken();
        if(authDAO.getAuth(authToken) == null){
            throw new UnauthorizedException("Unauthorized");
        }
        return new ListGamesResult(gameDAO.listGames());
    }
}
