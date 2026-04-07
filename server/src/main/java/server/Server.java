package server;

import dataaccess.*;
import dataaccess.memoryaccess.MemoryAuthDAO;
import dataaccess.memoryaccess.MemoryGameDAO;
import dataaccess.memoryaccess.MemoryUserDAO;
import dataaccess.sqlaccess.SQLAuthDao;
import dataaccess.sqlaccess.SQLGameDAO;
import dataaccess.sqlaccess.SQLUserDao;
import io.javalin.*;
import model.UserData;
import server.websocket.WebSocketHandler;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;
    private final ClearService clearService;
    private final GameService gameService;
    private final UserService userService;

    public Server(){
        try{
            DatabaseManager.configureDatabase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        GameDAO gameDAO = new SQLGameDAO();
        UserDAO userDAO = new SQLUserDao();
        AuthDAO authDAO = new SQLAuthDao();
        clearService = new ClearService(userDAO, gameDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(userDAO, authDAO);
        ClearHandler clearHandler = new ClearHandler(clearService);
        UserHandler userHandler = new UserHandler(userService);
        GameHandler gameHandler = new GameHandler(gameService);
        WebSocketHandler webSocketHandler = new WebSocketHandler(gameDAO, authDAO);

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", clearHandler::handleClear);
        javalin.post("/session", userHandler::handleLogin);
        javalin.post("/user", userHandler::handleRegister);
        javalin.delete("/session", userHandler::handleLogout);
        javalin.get("/game", gameHandler::handleListGames);
        javalin.post("/game", gameHandler::handleCreateGame);
        javalin.put("/game", gameHandler::handleJoinGame);
        javalin.ws("/ws", ws -> {
            ws.onConnect(webSocketHandler);
            ws.onMessage(webSocketHandler);
            ws.onClose(webSocketHandler);
        });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
