package server;

import dataaccess.GameDAO;
import dataaccess.memoryaccess.MemoryAuthDAO;
import dataaccess.memoryaccess.MemoryGameDAO;
import dataaccess.memoryaccess.MemoryUserDAO;
import io.javalin.*;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final ClearService clearService;
    private final GameService gameService;
    private final UserService userService;

    public Server() {

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        MemoryGameDAO gameDAO = new MemoryGameDAO();
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        clearService = new ClearService(userDAO, gameDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);
        ClearHandler clearHandler = new ClearHandler(clearService);
        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", clearHandler::handleClear);
        javalin.post("/session", userHandler::handleLogin);
        javalin.post("/user", userHandler::handleRegister);
        javalin.delete("/session", userHandler::handleLogout);
        javalin.get("/game", gameHandler::handleListGames);
        javalin.post("/game", gameHandler::handleCreateGame);
        javalin.put("/game", gameHandler::handleJoinGame);


    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
