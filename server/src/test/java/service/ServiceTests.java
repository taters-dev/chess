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

    @BeforeEach
    public void setup(){
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();
        clearService = new ClearService(userDAO, gameDAO, authDAO);
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
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(testUser.username()));
    }
}
