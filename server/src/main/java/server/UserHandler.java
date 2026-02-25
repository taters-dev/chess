package server;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import requestandresult.*;
import service.UserService;

public class UserHandler {
    private final UserService userService;
    private final Gson gson = new Gson();
    private final ExceptionHandler exceptionHandler = new ExceptionHandler();

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
            exceptionHandler.errorHelper(ctx, e);
        }
        catch(BadRequestException e){
            ctx.status(400);
            exceptionHandler.errorHelper(ctx, e);

        }
        catch(DataAccessException e){
            ctx.status(500);
            exceptionHandler.errorHelper(ctx, e);

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
            exceptionHandler.errorHelper(ctx, e);

        }catch(AlreadyTakenException e){
            ctx.status(403);
            exceptionHandler.errorHelper(ctx, e);
        }catch(DataAccessException e){
            ctx.status(500);
            exceptionHandler.errorHelper(ctx, e);
        }

    }

    public void handleLogout(Context ctx) throws UnauthorizedException, DataAccessException{
        try{
            LogoutRequest logoutRequest = new LogoutRequest(ctx.header("authorization"));

            userService.logout(logoutRequest);

            ctx.status(200);
            ctx.result("{}");

        }catch (UnauthorizedException e){
            ctx.status(401);
            exceptionHandler.errorHelper(ctx, e);


        } catch (DataAccessException e) {
            ctx.status(500);
            exceptionHandler.errorHelper(ctx, e);
        }
    }
}
