package service;

import chess.ChessGame;
import dataAccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import requestAndResult.*;

import java.util.ArrayList;
import java.util.Collection;

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
        gameService = new GameService(authDAO, gameDAO);
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
        Assertions.assertNull(authDAO.getAuth(testAuth.authToken()));
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

        RegisterRequest registerRequest = new RegisterRequest("taters", "pass", "novatate@byu.edu");
        try{
            userService.register(registerRequest);
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

        RegisterRequest registerRequest = new RegisterRequest("taters", "pass", "novatate@byu.edu");

        Assertions.assertThrows(AlreadyTakenException.class, () -> userService.register(registerRequest));
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

        LoginRequest loginRequest = new LoginRequest("taters", "pass");
        Assertions.assertDoesNotThrow(() -> userService.login(loginRequest));
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

        LoginRequest loginRequest = new LoginRequest("taters", "password");
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.login(loginRequest));
    }

    @Test
    @Order(6)
    @DisplayName("Logged out")
    public void validLogout(){
        AuthData testAuth = new AuthData("123", "taters");

        try{
            authDAO.createAuth(testAuth);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        LogoutRequest logoutRequest = new LogoutRequest("123");

        Assertions.assertDoesNotThrow(() -> userService.logout(logoutRequest));
    }

    @Test
    @Order(7)
    @DisplayName("Invalid Logout")
    public void invalidLogout(){
        LogoutRequest logoutRequest = new LogoutRequest("123");
        Assertions.assertThrows(UnauthorizedException.class, () -> userService.logout(logoutRequest));
    }

    @Test
    @Order(8)
    @DisplayName("List Games")
    public void validListGames() throws DataAccessException, UnauthorizedException {
        GameData gameOne = new GameData(1, "byu", "utah", "holywar", new ChessGame());
        GameData gameTwo = new GameData(2, "seahawks", "patriots", "superbowl", new ChessGame());
        Collection<GameData> testList = new ArrayList<>();
        testList.add(gameOne);
        testList.add(gameTwo);

        AuthData testData = new AuthData("123", "taters");
        authDAO.createAuth(testData);
        gameDAO.createGame(gameOne);
        gameDAO.createGame(gameTwo);

        ListGamesRequest listGamesRequest = new ListGamesRequest("123");
        ListGamesResult listGamesResult = new ListGamesResult(testList);

        Assertions.assertEquals(listGamesResult.toString(), gameService.listGames(listGamesRequest).toString());
    }

    // @Test
    // @Order(9)
}
