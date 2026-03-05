package dataaccess;

import dataaccess.sqlaccess.SQLAuthDao;
import dataaccess.sqlaccess.SQLGameDAO;
import dataaccess.sqlaccess.SQLUserDao;
import model.UserData;
import org.junit.jupiter.api.*;
import passoff.model.TestUser;
import passoff.server.TestServerFacade;
import server.Server;

public class SQLTests {

    private static final UserData TEST_USER = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");

    private static TestServerFacade serverFacade;

    private static Server server;

    private static GameDAO gameDAO;
    private static UserDAO userDAO;
    private static AuthDAO authDAO;

    @BeforeAll
    public static void startServer() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);

        gameDAO = new SQLGameDAO();
        userDAO = new SQLUserDao();
        authDAO = new SQLAuthDao();

        serverFacade = new TestServerFacade("localhost", Integer.toString(port));
    }

    @BeforeEach
    public void setUp() throws DataAccessException{
        try {
            userDAO.clear();
            gameDAO.clear();
            authDAO.clear();
        }catch (DataAccessException e){
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    @DisplayName("Successful createuser()")
    @Order(1)

    public void successCreateUser(){
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(TEST_USER));
    }

    @Test
    @DisplayName("Failure createuser()")
    @Order(2)

    public void failCreateUser() throws DataAccessException{
        try {
            userDAO.createUser(TEST_USER);
        } catch (DataAccessException e){
            throw new DataAccessException(e.getMessage(), e);
        }

        Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(TEST_USER));
    }

}
