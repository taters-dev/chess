package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import requestandresult.LoginResult;
import requestandresult.RegisterRequest;
import requestandresult.RegisterResult;
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
}
