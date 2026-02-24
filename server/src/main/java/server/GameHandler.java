package server;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import requestandresult.*;
import service.GameService;

import java.util.Map;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public GameHandler(GameService gameService){
        this.gameService = gameService;
    }

    public void handleListGames(Context ctx){
        try{
            ListGamesRequest listGamesRequest = new ListGamesRequest(ctx.header("authorization"));
            ListGamesResult listGamesResult = gameService.listGames(listGamesRequest);
            ctx.status(200);
            ctx.json(listGamesResult);
        }catch(UnauthorizedException e){
            ctx.status(401);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }catch (DataAccessException e){
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public void handleCreateGame(Context ctx){
        try{
            CreateGameRequest body = ctx.bodyAsClass(CreateGameRequest.class);
            String authToken = ctx.header("authorization");
            CreateGameRequest createGameRequest = new CreateGameRequest(authToken, body.gameName());
            CreateGameResult createGameResult = gameService.createGame(createGameRequest);
            ctx.status(200);
            ctx.json(createGameResult);
        } catch(BadRequestException e){
            ctx.status(400);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        } catch (UnauthorizedException e){
            ctx.status(401);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        } catch (DataAccessException e){
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public void handleJoinGame(Context ctx){
        try{
            JoinGameRequest  body = ctx.bodyAsClass(JoinGameRequest.class);
            String authToken = ctx.header("authorization");
            JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, body.playerColor(), body.gameID());
            if(body.playerColor() == null || (!body.playerColor().equals("WHITE") && !body.playerColor().equals("BLACK"))){
                throw new BadRequestException("Incorrect Color");
            }
            gameService.joinGame(joinGameRequest);
            ctx.status(200);
            ctx.json(Map.of());
        } catch (BadRequestException e){
            ctx.status(400);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        } catch (UnauthorizedException e){
            ctx.status(401);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        } catch (AlreadyTakenException e){
            ctx.status(403);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        } catch (DataAccessException e){
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
