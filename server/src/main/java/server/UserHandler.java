package server;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import requestandresult.*;
import service.UserService;

import java.util.HashMap;
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

            var result = gson.toJson(loginResult);

            ctx.result(result);

        }catch(UnauthorizedException e){
            ctx.status(401);

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);
        }
        catch(BadRequestException e){
            ctx.status(400);

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);

        }
        catch(DataAccessException e){
            ctx.status(500);

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);

        }
    }

    public void handleRegister(Context ctx){
        try{
            RegisterRequest registerRequest = gson.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult registerResult = userService.register(registerRequest);
            ctx.status(200);

            var result = gson.toJson(registerResult);

            ctx.json(result);
        } catch(BadRequestException e){
            ctx.status(400);

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);

        }catch(AlreadyTakenException e){
            ctx.status(403);

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);

        }catch(DataAccessException e){
            ctx.status(500);

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);
        }

    }

    public void handleLogout(Context ctx){
        try{
            LogoutRequest logoutRequest = new LogoutRequest(ctx.header("authorization"));

            userService.logout(logoutRequest);

            ctx.status(200);
            ctx.result("{}");
        }catch (UnauthorizedException e){
            ctx.status(401);

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);
        } catch (DataAccessException e) {
            ctx.status(500);

            Map<String, String> mappedResult = new HashMap<>();
            mappedResult.put("message", "Error: " + e.getMessage());
            var result = gson.toJson(mappedResult);

            ctx.result(result);
        }
    }

}
