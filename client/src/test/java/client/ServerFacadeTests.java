package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import requestandresult.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(port);
    }

    @BeforeEach
    void cleanup() throws ResponseException {
        serverFacade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }



    @Test
    public void clearTest() throws Exception{
        serverFacade.register("tate", "password",
                "novatate@byu.edu");
        Assertions.assertThrows(Exception.class, () -> serverFacade.register("tate", "password",
                "novatate@byu.edu"));

        serverFacade.clear();
        Assertions.assertDoesNotThrow(() -> serverFacade.register("tate", "password",
                "novatate@byu.edu"));
    }

    @Test
    public void positiveLogin() throws ResponseException{
        serverFacade.register("tate", "password",
                "novatate@byu.edu");
        LoginResult loginResult = serverFacade.login("tate", "password");
        Assertions.assertTrue(loginResult.authToken() != null);
    }

    @Test
    public void failedLogin() throws ResponseException{
        serverFacade.register("tate", "password",
                "novatate@byu.edu");
        Assertions.assertThrows(Exception.class, () -> serverFacade.login("taters", "password"));
    }

    @Test
    public void positiveRegister() throws ResponseException{
       RegisterResult registerResult = serverFacade.register("tate", "password",
               "novatate@byu.edu");
       Assertions.assertTrue(registerResult.authToken() != null);
    }

    @Test
    public void failedRegister() throws ResponseException{
        serverFacade.register("tate", "password",
                "novatate@byu.edu");

        Assertions.assertThrows(Exception.class, () -> serverFacade.register("tate", "password",
                "novatate@byu.edu"));
    }

    @Test
    public void positiveLogout() throws ResponseException{
        RegisterResult registerResult = serverFacade.register("tate", "password", "novatate@byu.edu");
        Assertions.assertDoesNotThrow(() -> serverFacade.logout(registerResult.authToken()));
        Assertions.assertDoesNotThrow(() -> serverFacade.login("tate", "password"));
    }

    @Test
    public void failedLogout() throws ResponseException{
        RegisterResult registerResult = serverFacade.register("tate", "password", "novatate@byu.edu");
        String fakeAuth = registerResult.authToken() + 1;
        Assertions.assertThrows(Exception.class, () -> serverFacade.logout(fakeAuth));
        Assertions.assertDoesNotThrow(() -> serverFacade.logout(registerResult.authToken()));
    }

    @Test
    public void positiveListGames() throws ResponseException{
        RegisterResult registerResult = serverFacade.register("tate", "password", "novatate@byu.edu");
        String authToken = registerResult.authToken();
        serverFacade.createGame(authToken, "game name");
        var listOfGames = serverFacade.listGames(authToken);
        Assertions.assertDoesNotThrow(() -> serverFacade.listGames(authToken));
        Assertions.assertNotEquals(null, listOfGames);
    }

    @Test
    public void failedListGames() throws ResponseException {
        Assertions.assertThrows(Exception.class, () -> serverFacade.listGames("123"));
    }

    @Test
    public void positiveCreateGame() throws ResponseException{
        RegisterResult registerResult = serverFacade.register("tate", "password", "novatate@byu.edu");
        String authToken = registerResult.authToken();
        Assertions.assertDoesNotThrow(() -> serverFacade.createGame(authToken, "march madness"));
    }

    @Test
    public void failedCreateGame() throws ResponseException{
        Assertions.assertThrows(Exception.class, () -> serverFacade.createGame("GOCOUGARS", "Minecraft"));
    }

    @Test
    public void positiveJoinGame() throws ResponseException{
        RegisterResult registerResult = serverFacade.register("tate", "password", "novatate@byu.edu");
        CreateGameResult createGameResult = serverFacade.createGame(registerResult.authToken(), "taters");
        var oldGames = serverFacade.listGames(registerResult.authToken());
        Assertions.assertDoesNotThrow(() -> serverFacade.joinGame(registerResult.authToken(),
                "WHITE", createGameResult.gameID()));
        Assertions.assertNotEquals(oldGames, serverFacade.listGames(registerResult.authToken()));
    }

    @Test
    public void failedJoinGame() throws ResponseException{
        RegisterResult registerResult = serverFacade.register("tate", "password", "novatate@byu.edu");
        CreateGameResult createGameResult = serverFacade.createGame(registerResult.authToken(), "taters");
        serverFacade.joinGame(registerResult.authToken(), "WHITE", createGameResult.gameID());
        Assertions.assertThrows(Exception.class, () -> serverFacade.joinGame(registerResult.authToken(),
                "WHITE", createGameResult.gameID()));
    }
}
