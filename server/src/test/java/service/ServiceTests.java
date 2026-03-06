package service;

import chess.ChessGame;
import dataaccess.*;
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
import requestandresult.*;

import java.util.ArrayList;
import java.util.Collection;

public class ServiceTests {
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    private UserService userService;
    private GameService gameService;
    private ClearService clearService;

    @AfterEach
    public void cleanup() throws DataAccessException{
        clearService.clear();
    }
    @BeforeEach
    public void setup() throws DataAccessException{
        authDAO = new SQLAuthDao();
        gameDAO = new SQLGameDAO();
        userDAO = new SQLUserDao();
        clearService = new ClearService(userDAO, gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(authDAO, gameDAO);
        clearService.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Clear Service")
    public void clearServiceSuccess() throws DataAccessException{
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

        clearService.clear();

        Assertions.assertEquals(0, gameDAO.listGames().size());
        Assertions.assertNull(authDAO.getAuth(testAuth.authToken()));
        Assertions.assertNull(gameDAO.getGame(testGame.gameID()));
        Assertions.assertNull(userDAO.getUser(testUser.username()));
    }

    @Test
    @Order(2)
    @DisplayName("Valid Registration")
    public void validRegistration() throws Exception{
        UserData testUser = new UserData("taters", "pass", "novatate@byu.edu");

        RegisterRequest registerRequest = new RegisterRequest("taters", "pass", "novatate@byu.edu");
        Assertions.assertDoesNotThrow(() -> userService.register(registerRequest));
        UserData newUser = userDAO.getUser(testUser.username());
        Assertions.assertTrue(BCrypt.checkpw(testUser.password(), newUser.password()));
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

        AuthData testData = new AuthData("123", "taters");
        authDAO.createAuth(testData);
        int firstID = gameDAO.createGame(gameOne);
        int secondID = gameDAO.createGame(gameTwo);

        gameOne = new GameData(firstID, gameOne.whiteUsername(), gameOne.blackUsername(), gameOne.gameName(), gameOne.game());
        gameTwo = new GameData(secondID, gameTwo.whiteUsername(), gameTwo.blackUsername(), gameTwo.gameName(), gameTwo.game());

        testList.add(gameOne);
        testList.add(gameTwo);


        ListGamesRequest listGamesRequest = new ListGamesRequest("123");
        ListGamesResult listGamesResult = new ListGamesResult(testList);

        Assertions.assertEquals(listGamesResult, gameService.listGames(listGamesRequest));
    }

    @Test
    @Order(9)
    @DisplayName("Invalid List Games")
    public void invalidListGames() throws DataAccessException, UnauthorizedException {
        ListGamesRequest listGamesRequest = new ListGamesRequest("123");
        Assertions.assertThrows(UnauthorizedException.class, () -> gameService.listGames(listGamesRequest));
    }

    @Test
    @Order(10)
    @DisplayName("Create valid game")
    public void validCreateGame() throws DataAccessException, UnauthorizedException {
        CreateGameRequest createGameRequest = new CreateGameRequest("123", "superbowl");
        AuthData authData = new AuthData("123", "taters");
        authDAO.createAuth(authData);

        Assertions.assertDoesNotThrow(() -> gameService.createGame(createGameRequest));
    }

    @Test
    @Order(11)
    @DisplayName("Invalid game creation")
    public void invalidCreateGame(){
        Assertions.assertThrows(UnauthorizedException.class, () -> gameService.createGame(new CreateGameRequest("123", "taters")));
    }

    @Test
    @Order(12)
    @DisplayName("Valid Join Game")
    public void validJoinGame() throws DataAccessException, AlreadyTakenException, BadRequestException, UnauthorizedException{
        AuthData authData = new AuthData("123", "taters");
        GameData gameData = new GameData(2, null, "tots", "tatertots", new ChessGame());
        authDAO.createAuth(authData);
        int gameID = gameDAO.createGame(gameData);
        System.out.println(gameData.gameID());
        Assertions.assertDoesNotThrow(() -> gameService.joinGame(new JoinGameRequest("123", "WHITE", gameID)));
    }

    @Test
    @Order(13)
    @DisplayName("Invalid Join Game")
    public void invalidJoinGame(){
        Assertions.assertThrows(UnauthorizedException.class,
                () -> gameService.joinGame(new JoinGameRequest("123", "WHITE", 2)));
    }
}
