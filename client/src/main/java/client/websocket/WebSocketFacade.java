package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import exception.ResponseException;
import jakarta.websocket.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.net.URI;

public class WebSocketFacade extends Endpoint {

    Session session;
    ServerMessageHandler serverMessageHandler;
    String websocketURL;

    public WebSocketFacade(int port, ServerMessageHandler serverMessageHandler) throws Exception{
        this.websocketURL = "ws://localhost:" + port + "/ws";
        this.serverMessageHandler = serverMessageHandler;

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, new URI(websocketURL));

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
                serverMessageHandler.notify(serverMessage);
            }
        });
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken, int gameID) throws Exception{
        try{
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception e){
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

    public void leave(String authToken, int gameID) throws Exception{
        try{
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception e){
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws Exception{
        try{
            var userGameCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(userGameCommand));
        } catch (Exception e){
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws Exception{
        try{
            var makeMoveCommand = new MakeMoveCommand(authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(makeMoveCommand));
        } catch (Exception e){
            throw new ResponseException(ResponseException.Code.ServerError, e.getMessage());
        }
    }
}
