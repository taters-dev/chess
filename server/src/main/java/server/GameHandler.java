package server;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import requestandresult.*;
import service.GameService;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();
    private final ExceptionHandler exceptionHandler = new ExceptionHandler();

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
            exceptionHandler.errorHelper(ctx, e);

        }catch (DataAccessException e){
            ctx.status(500);
            exceptionHandler.errorHelper(ctx, e);
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
            exceptionHandler.errorHelper(ctx, e);

        } catch (UnauthorizedException e){
            ctx.status(401);
            exceptionHandler.errorHelper(ctx, e);

        } catch (DataAccessException e){
            ctx.status(500);
            exceptionHandler.errorHelper(ctx, e);

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
            exceptionHandler.errorHelper(ctx, e);

        } catch (UnauthorizedException e){
            ctx.status(401);
            exceptionHandler.errorHelper(ctx, e);

        } catch (AlreadyTakenException e){
            ctx.status(403);
            exceptionHandler.errorHelper(ctx, e);

        } catch (DataAccessException e){
            ctx.status(500);
            exceptionHandler.errorHelper(ctx, e);

        }
    }
}
