package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import requestandresult.*;

public class GameService {
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO){
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException,
            UnauthorizedException {
        String authToken = listGamesRequest.authToken();
        if(authDAO.getAuth(authToken) == null){
            throw new UnauthorizedException("Unauthorized");
        }
        return new ListGamesResult(gameDAO.listGames());
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws UnauthorizedException,
            DataAccessException {
        String authToken = createGameRequest.authToken();
        String gameName = createGameRequest.gameName();

        if(authDAO.getAuth(authToken) == null){
            throw new UnauthorizedException("Unauthorized");
        }
        GameData newGame = new GameData(0, null, null, gameName, new ChessGame());
        int gameID = gameDAO.createGame(newGame);

        return new CreateGameResult(gameID);
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws AlreadyTakenException, BadRequestException,
            UnauthorizedException, DataAccessException{


        String authToken = joinGameRequest.authToken();
        String playerColor = joinGameRequest.playerColor();
        int gameID = joinGameRequest.gameID();


        if(authDAO.getAuth(authToken) == null){
            throw new UnauthorizedException("Unauthorized");
        }

        String username = authDAO.getAuth(authToken).username();

        if(playerColor == null){
            throw new BadRequestException("Player Color was not provided");

        }

        GameData game = gameDAO.getGame(gameID);

        if(game == null){
            throw new BadRequestException("Game does not exist");
        }
        System.out.println(gameDAO.getGame(gameID).whiteUsername());
        if(playerColor.equals("WHITE")){
            if(game.whiteUsername() != null){
                System.out.println(game.whiteUsername());
                System.out.println(game.blackUsername());
                System.out.println("FIUHHHHH");
                throw new AlreadyTakenException("This spot is already taken");
            }
            game = new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game());
            gameDAO.updateGame(game);
        }
        else{
            if(game.blackUsername() != null){
                throw new AlreadyTakenException("This spot is already taken");
            }
            game = new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game());
            gameDAO.updateGame(game);
        }

    }
}
