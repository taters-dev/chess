package dataaccess;

import chess.ChessGame;
import dataaccess.memoryaccess.MemoryAuthDAO;
import dataaccess.memoryaccess.MemoryGameDAO;
import dataaccess.memoryaccess.MemoryUserDAO;
import dataaccess.sqlaccess.SQLAuthDao;
import dataaccess.sqlaccess.SQLGameDAO;
import dataaccess.sqlaccess.SQLUserDao;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import server.Server;

import java.util.ArrayList;
import java.util.Collection;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SQLTests {

    private static final UserData TEST_USER = new UserData("ExistingUser", "existingUserPassword", "eu@mail.com");
    private static final AuthData TEST_AUTH = new AuthData("auth", "ExistingUser");
    private static final GameData TEST_GAME = new GameData(1, null, null, "test", new ChessGame());

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
            Assertions.assertThrows(DataAccessException.class, () -> userDAO.createUser(TEST_USER));
        } catch (DataAccessException e){
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Test
    @DisplayName("Get User Success")
    @Order(3)

    public void successGetUser() throws DataAccessException{
        try{
            userDAO.createUser(TEST_USER);
            UserData result = userDAO.getUser(TEST_USER.username());
            Assertions.assertTrue(result.username().equals(TEST_USER.username()) &&
                            BCrypt.checkpw(TEST_USER.password(), result.password()) &&
                            result.email().equals(TEST_USER.email())
                    );
        } catch (DataAccessException e){
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Test
    @DisplayName("Get User Doesn't Exist")
    @Order(4)

    public void failGetUser() throws DataAccessException{
        var result = userDAO.getUser(TEST_USER.username());
        Assertions.assertNull(result);
    }

    @Test
    @DisplayName("Clear Users")
    @Order(5)

    public void clearUsers() throws DataAccessException{
        try{
            userDAO.createUser(TEST_USER);
            userDAO.clear();
            Assertions.assertDoesNotThrow(() -> userDAO.createUser(TEST_USER));
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage(), e);
        }

    }

    @Test
    @DisplayName("Create Auth Success")
    @Order(6)

    public void createAuth() throws DataAccessException{
        try{
            authDAO.createAuth(TEST_AUTH);
            Assertions.assertEquals(TEST_AUTH, authDAO.getAuth(TEST_AUTH.authToken()));
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage(), e);
        }

    }

    @Test
    @DisplayName("Auth Creation Failure")
    @Order(7)

    public void failedAuth() throws DataAccessException{
        try{
            authDAO.createAuth(TEST_AUTH);
            Assertions.assertThrows(Exception.class, () -> authDAO.createAuth(TEST_AUTH));
        } catch (DataAccessException e){
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Test
    @DisplayName("Auth Deletion Success")
    @Order(8)

    public void successDeleteAuth() throws DataAccessException{
        try{
            authDAO.createAuth(TEST_AUTH);
            authDAO.deleteAuth(TEST_AUTH.authToken());
            Assertions.assertDoesNotThrow(() -> authDAO.createAuth(TEST_AUTH));
        } catch(DataAccessException e){
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Test
    @DisplayName("Auth Deletion Doesn't Effect Other Auth")
    @Order(9)

    public void failedDeleteAuth() throws DataAccessException{
        authDAO.createAuth(TEST_AUTH);
        authDAO.deleteAuth("DOESN'T EXIST");
        Assertions.assertDoesNotThrow(() -> authDAO.getAuth(TEST_AUTH.authToken()));
    }

    @Test
    @DisplayName("Clear Auth")
    @Order(10)

    public void clearAuth() throws DataAccessException{
        try{
            authDAO.createAuth(TEST_AUTH);
            authDAO.clear();
            Assertions.assertDoesNotThrow(() -> authDAO.createAuth(TEST_AUTH));
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage(), e);
        }

    }

    @Test
    @DisplayName("Create Game Success")
    @Order(11)
    public void createGameSuccess() throws DataAccessException{
        int id = gameDAO.createGame(TEST_GAME);
        Assertions.assertDoesNotThrow(() -> gameDAO.createGame(TEST_GAME));
        Assertions.assertDoesNotThrow(() -> gameDAO.getGame(id));
    }

    @Test
    @DisplayName("Create Game All Null")
    @Order(12)
    public void createNullGame(){
        Assertions.assertThrows(Exception.class, () -> gameDAO.createGame(new GameData(0, null, null, null, null)));
    }

    @Test
    @DisplayName("Clear Games")
    @Order(13)
    public void clearGames() throws DataAccessException{
        try{
            gameDAO.createGame(TEST_GAME);
            gameDAO.clear();
            Assertions.assertDoesNotThrow(() -> gameDAO.createGame(TEST_GAME));
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage(), e);
        }
    }

    @Test
    @DisplayName("Get Game Success")
    @Order(14)
    public void successGetGame() throws DataAccessException{
        gameDAO.createGame(TEST_GAME);
        Assertions.assertDoesNotThrow(() -> gameDAO.getGame(1));
    }

    @Test
    @DisplayName("Get Game Failure")
    @Order(15)
    public void failureGetGame() throws DataAccessException{
        gameDAO.createGame(TEST_GAME);
        var result = gameDAO.getGame(0);
        Assertions.assertNull(result);
    }

    @Test
    @DisplayName("Get auth success")
    @Order(16)

    public void successGetAuth() throws DataAccessException{
        authDAO.createAuth(TEST_AUTH);
        Assertions.assertDoesNotThrow(() -> authDAO.getAuth(TEST_AUTH.authToken()));
    }

    @Test
    @DisplayName("Get auth fail")
    @Order(17)

    public void failGetAuth() throws DataAccessException{
        Assertions.assertNull(authDAO.getAuth("fail"));
    }

    @Test
    @DisplayName("Update game success")
    @Order(18)
    public void successUpdateGame() throws DataAccessException{
        int id = gameDAO.createGame(TEST_GAME);
        GameData update = new GameData(id, "tate", null, TEST_GAME.gameName(), TEST_GAME.game());
        gameDAO.updateGame(update);
        GameData curr = gameDAO.getGame(id);
        Assertions.assertEquals(update.whiteUsername(), curr.whiteUsername());
    }

    @Test
    @DisplayName("Update game failure")
    @Order(19)
    public void failUpdateGame() throws DataAccessException {
        gameDAO.updateGame(TEST_GAME);
        var result = gameDAO.getGame(TEST_GAME.gameID());
        Assertions.assertNull(result);
    }

    @Test
    @DisplayName("List games success")
    @Order(20)
    public void successListGames() throws DataAccessException{
        gameDAO.createGame(TEST_GAME);
        Collection<GameData> result = gameDAO.listGames();

        Assertions.assertEquals(1, result.size());
        Assertions.assertTrue(result.contains(TEST_GAME));
    }

    @Test
    @DisplayName("List game empty table")
    @Order(21)
    public void emptyListGames() throws DataAccessException{
        Collection<GameData> result = gameDAO.listGames();
        Assertions.assertEquals(0, result.size());
    }

}
