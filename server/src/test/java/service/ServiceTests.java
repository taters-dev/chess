package service;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

public class ServiceTests {
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private MemoryUserDAO userDAO;

    private UserService userService;
    private GameService gameService;
    private ClearService clearService;

    @AfterEach
    public void cleanup(){
        clearService.clear();
    }
    @BeforeEach
    public void setup(){
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();
        clearService = new ClearService(userDAO, gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
        clearService.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Clear Service")
    public void clearServiceSuccess(){
        ChessGame game = new ChessGame();
        AuthData testAuth = new AuthData("123", "taters");
        GameData testGame = new GameData(123, "taters", "tots", "Holy War", game);
        UserData testUser = new UserData("taters", "pass", "novatate@byu.edu");
        try{
            authDAO.createAuth(testAuth);
            gameDAO.createGame(testGame);
            userDAO.createUser(testUser);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        System.out.println(gameDAO.listGames().size());
        clearService.clear();
        System.out.println(gameDAO.listGames().size());

        Assertions.assertEquals(0, gameDAO.listGames().size());
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(testAuth.authToken()));
        Assertions.assertThrows(DataAccessException.class, () -> gameDAO.getGame(testGame.gameID()));
        Assertions.assertNull(userDAO.getUser(testUser.username()));
    }

    @Test
    @Order(2)
    @DisplayName("Valid Registration")
    public void validRegistration(){
        UserData testUser = new UserData("taters", "pass", "novatate@byu.edu");
        System.out.println(testUser.toString());
        System.out.println(userDAO.getUser(testUser.username()));
        try{
            userService.register("taters", "pass", "novatate@byu.edu");
        } catch (BadRequestException | DataAccessException |AlreadyTakenException e) {
            throw new RuntimeException(e);
        }

        System.out.println(userDAO.getUser(testUser.username()));

        Assertions.assertEquals(testUser, userDAO.getUser(testUser.username()));
    }

    @Test
    @Order(3)
    @DisplayName("Invalid Registration - Existing Username")
    public void invalidRegistration(){
        UserData testUser = new UserData("taters", "pass", "novatate@byu.edu");
        try{
            userDAO.createUser(testUser);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertThrows(AlreadyTakenException.class, () -> userService.register("taters", "pass", "novatate@byu.edu"));
    }

    @Test
    @Order(4)
    @DisplayName("Valid Login")
    public void validLogin(){
        UserData testUser = new UserData("taters", "pass", "novatate@byu.edu");
        try{
            userDAO.createUser(testUser);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertDoesNotThrow(() -> userService.login("taters", "pass"));
    }

    @Test
    @Order(5)
    @DisplayName("Invalid Password")
    public void invalidLogin(){
        UserData testUser = new UserData("taters", "pass", "novatate@byu.edu");
        try{
            userDAO.createUser(testUser);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.login("taters", "password"));
    }


}
