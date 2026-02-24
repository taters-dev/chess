package server;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import requestandresult.*;
import service.UserService;

import java.util.Map;

public class UserHandler {
    private final UserService userService;
    private final Gson gson = new Gson();

    public UserHandler(UserService userService){
        this.userService = userService;
    }

    public void handleLogin(Context ctx){
        try{
            LoginRequest loginRequest = gson.fromJson(ctx.body(), LoginRequest.class);
            LoginResult loginResult = userService.login(loginRequest);
            ctx.status(200);
            ctx.json(loginResult);

        }catch(UnauthorizedException e){
            ctx.status(401);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
        catch(BadRequestException e){
            ctx.status(400);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
        catch(DataAccessException e){
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public void handleRegister(Context ctx){
        try{
            RegisterRequest registerRequest = gson.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult registerResult = userService.register(registerRequest);
            ctx.status(200);
            ctx.json(registerResult);
        } catch(BadRequestException e){
            ctx.status(400);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }catch(AlreadyTakenException e){
            ctx.status(403);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }catch(DataAccessException e){
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }

    }

    public void handleLogout(Context ctx){
        try{
            LogoutRequest logoutRequest = new LogoutRequest(ctx.header("authorization"));
            userService.logout(logoutRequest);
            ctx.status(200);
            ctx.json(Map.of());
        }catch (UnauthorizedException e){
            ctx.status(401);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

}
