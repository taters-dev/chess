package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(GameDAO gameDAO, AuthDAO authDAO){
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }
    @Override
    public void handleClose(@NotNull WsCloseContext ctx){
        System.out.println("Websocket closed");
    }

    @Override
    public void handleConnect(@NotNull WsConnectContext ctx){
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) throws Exception{
        UserGameCommand gameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        MakeMoveCommand moveCommand = new MakeMoveCommand(null, null, null);
        if(gameCommand.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE){
            moveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
        }

        String authToken = gameCommand.getAuthToken();
        int gameID = gameCommand.getGameID();
        Session session = ctx.session;
        ChessMove move = moveCommand.getMove();

        switch (gameCommand.getCommandType()){
            case CONNECT -> connect(authToken, gameID, session);
            case LEAVE -> leave(authToken, gameID, session);
            case MAKE_MOVE -> makeMove(authToken, gameID, session, move);
            case RESIGN -> resign(authToken, gameID, session);
        }

    }

    private void connect(String authToken, int gameID, Session session) throws Exception {

        AuthData authData = authDAO.getAuth(authToken);
        GameData gameData = gameDAO.getGame(gameID);


        if(!errorCheck(authData, gameData, session)){
            String user = authData.username();
            LoadGameMessage loadGameMessage = new LoadGameMessage(gameData.game());
            NotificationMessage notificationMessage;

            if(user.equals(gameData.whiteUsername())){
                notificationMessage = new NotificationMessage(user + " joined the game as white player");
            }
            else if(user.equals(gameData.blackUsername())){
                notificationMessage = new NotificationMessage(user + " joined the game as black player");
            }
            else{
                notificationMessage = new NotificationMessage(user + " joined the game as an observer");
            }

            connections.add(gameID, user, session);
            connections.sendMessage(gameID, user, loadGameMessage);
            connections.broadcastMessage(gameID, user, notificationMessage);


        }
    }


    private void leave(String authToken, int gameID, Session session) throws Exception{
        AuthData authData = authDAO.getAuth(authToken);
        GameData gameData = gameDAO.getGame(gameID);

       if(!errorCheck(authData, gameData, session)){
            String user = authData.username();
            NotificationMessage notificationMessage;

            if(user.equals(gameData.blackUsername())){
                notificationMessage = new NotificationMessage(user + " black player has left the game");
                gameData = new GameData(gameID, gameData.whiteUsername(), null,
                        gameData.gameName(), gameData.game());
                gameDAO.updateGame(gameData);
            }
            else if(user.equals(gameData.whiteUsername())){
                notificationMessage = new NotificationMessage(user + " white player has left the game");
                gameData = new GameData(gameID, null, gameData.blackUsername(),
                        gameData.gameName(), gameData.game());
                gameDAO.updateGame(gameData);
            }
            else{
                notificationMessage = new NotificationMessage(user + " is no longer observing");
            }


            connections.broadcastMessage(gameID, user, notificationMessage);
            connections.remove(gameID, user);
        }
    }


    private void resign(String authToken, int gameID, Session session) throws Exception{
        AuthData authData = authDAO.getAuth(authToken);
        GameData gameData = gameDAO.getGame(gameID);

        if(!errorCheck(authData, gameData, session)){
            String user = authData.username();
            if(!user.equals(gameData.whiteUsername()) && !user.equals(gameData.blackUsername())){
                ErrorMessage errorMessage = new ErrorMessage("Error: Observer cannot resign");
                connections.sendMessage(gameID, user, errorMessage);
            }
            else{
                ChessGame chessGame = gameData.game();
                if(!chessGame.isGameOver()){
                    chessGame.setGameOver(true);
                    gameDAO.updateGame(gameData);

                    NotificationMessage notificationMessage = new NotificationMessage(user +
                            " has resigned from the game");
                    connections.sendMessage(gameID, user, notificationMessage);
                    connections.broadcastMessage(gameID, user, notificationMessage);
                }
                else{
                    ErrorMessage errorMessage = new ErrorMessage("Error: Game is already over");
                    connections.sendMessage(gameID, user, errorMessage);
                }
            }
        }
    }


    private void makeMove(String authToken, int gameID, Session session, ChessMove chessMove) throws Exception{
        AuthData authData = authDAO.getAuth(authToken);
        GameData gameData = gameDAO.getGame(gameID);



        if(!errorCheck(authData, gameData, session)){
            String user = authData.username();
            ChessGame game = gameData.game();
            chess.ChessGame.TeamColor turn = game.getTeamTurn();
            var moves = game.validMoves(chessMove.getStartPosition());
            boolean validMove = moves.contains(chessMove);

            if(gameData.game().isGameOver()){
                ErrorMessage errorMessage = new ErrorMessage("Game is already over");
                connections.sendMessage(gameID,user, errorMessage);
            }

            else if(gameData.whiteUsername().equals(user) && turn == ChessGame.TeamColor.WHITE && validMove){
                moveHelper(chessMove, game, gameData, user);
            }
            else if(gameData.blackUsername().equals(user) && turn == ChessGame.TeamColor.BLACK && validMove){
                moveHelper(chessMove, game, gameData, user);
            }
            else{
                if(!user.equals(gameData.blackUsername()) && !user.equals(gameData.whiteUsername())){
                    ErrorMessage errorMessage = new ErrorMessage("Error: Observer cannot make move");
                    connections.sendMessage(gameID, user, errorMessage);
                }
                else if(!validMove){
                    ErrorMessage errorMessage = new ErrorMessage("Error: Invalid Move");
                    connections.sendMessage(gameID, user, errorMessage);
                }
                else{
                    ErrorMessage errorMessage = new ErrorMessage("Error: Not your turn");
                    connections.sendMessage(gameID, user, errorMessage);
                }
            }

            ChessGame.TeamColor opp = ChessGame.TeamColor.WHITE;
            if(turn == ChessGame.TeamColor.WHITE){
                opp = ChessGame.TeamColor.BLACK;
            }

            if(game.isInCheckmate(opp)){
                game.setGameOver(true);
                gameDAO.updateGame(gameData);
                NotificationMessage notificationMessage = new NotificationMessage("CHECKMATE! " + user + " wins!");
                connections.broadcastMessage(gameID, null, notificationMessage);
            }

            else if(game.isInStalemate(opp)){
                game.setGameOver(true);
                gameDAO.updateGame(gameData);
                NotificationMessage notificationMessage = new NotificationMessage("STALEMATE!");
                connections.broadcastMessage(gameID, null, notificationMessage);
            }
            else if(game.isGameOver()){
                System.out.println("YOOOOO");
            }
        }

    }

    private void moveHelper(ChessMove chessMove, ChessGame game, GameData gameData, String user) throws Exception{
        int gameID = gameData.gameID();
        game.makeMove(chessMove);
        gameDAO.updateGame(gameData);

        LoadGameMessage loadGameMessage = new LoadGameMessage(game);
        NotificationMessage notificationMessage = new NotificationMessage(user + " made move to " +
                chessMove.getEndPosition());

        connections.broadcastMessage(gameID, null, loadGameMessage);
        connections.broadcastMessage(gameID, user, notificationMessage);
    }


    private boolean errorCheck(AuthData authData, GameData gameData, Session session) throws Exception{
        if(authData == null){
            ErrorMessage errorMessage = new ErrorMessage("Error: Invalid auth token");
            String  msg = new Gson().toJson(errorMessage);
            session.getRemote().sendString(msg);
            return true;
        }
        else if(gameData == null){
            ErrorMessage errorMessage = new ErrorMessage("Error: Invalid Game ID");
            String  msg = new Gson().toJson(errorMessage);
            session.getRemote().sendString(msg);
            return true;
        }
        return false;
    }
}
