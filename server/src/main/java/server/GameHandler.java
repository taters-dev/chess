package server;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import requestandresult.*;
import service.GameService;

import java.util.HashMap;
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

            var result = gson.toJson(listGamesResult);
            ctx.result(result);

        }catch(UnauthorizedException e){
            ctx.status(401);

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);

        }catch (DataAccessException e){
            ctx.status(500);

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);
        }
    }

    public void handleCreateGame(Context ctx){
        try{
            CreateGameRequest body = gson.fromJson(ctx.body(), CreateGameRequest.class);
            String authToken = ctx.header("authorization");

            CreateGameRequest createGameRequest = new CreateGameRequest(authToken, body.gameName());
            CreateGameResult createGameResult = gameService.createGame(createGameRequest);

            var result = gson.toJson(createGameResult);

            ctx.status(200);
            ctx.result(result);

        } catch(BadRequestException e){
            ctx.status(400);

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);

        } catch (UnauthorizedException e){
            ctx.status(401);

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);

        } catch (DataAccessException e){
            ctx.status(500);

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);
        }
    }

    public void handleJoinGame(Context ctx){
        try{
            JoinGameRequest  body = gson.fromJson(ctx.body(), JoinGameRequest.class);
            String authToken = ctx.header("authorization");
            JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, body.playerColor(), body.gameID());

            if(body.playerColor() == null || (!body.playerColor().equals("WHITE") && !body.playerColor().equals("BLACK"))){
                throw new BadRequestException("Incorrect Color");
            }

            gameService.joinGame(joinGameRequest);

            ctx.status(200);
            ctx.result("{}");

        } catch (BadRequestException e){
            ctx.status(400);

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);
        } catch (UnauthorizedException e){
            ctx.status(401);

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);

        } catch (AlreadyTakenException e){
            ctx.status(403);

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);

        } catch (DataAccessException e){
            ctx.status(500);

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);
        }
    }
}
