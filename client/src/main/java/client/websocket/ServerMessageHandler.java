package client.websocket;

import chess.ChessGame;
import client.ChessClient;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

public class ServerMessageHandler {
    ChessClient chessClient;

    public ServerMessageHandler(ChessClient chessClient){
        this.chessClient = chessClient;
    }
    void notify(ServerMessage serverMessage){
       ServerMessage.ServerMessageType serverMessageType = serverMessage.getServerMessageType();
       switch (serverMessageType){
           case ERROR -> {
               ErrorMessage errorMessage = (ErrorMessage) serverMessage;
               String msg = errorMessage.getErrorMessage();
               System.out.println(msg);
           }
           case LOAD_GAME -> {
               LoadGameMessage loadGameMessage = (LoadGameMessage) serverMessage;
               ChessGame chessGame = ((LoadGameMessage) serverMessage).getGame();
               chessClient.setChessGame(chessGame);
               if(chessClient.getTeamColor() == null){
                   chessClient.drawBoard("WHITE");
               }
               else{
                   chessClient.drawBoard(chessClient.getTeamColor().toString());
               }
           }
           case NOTIFICATION ->{
               NotificationMessage notificationMessage = (NotificationMessage) serverMessage;
               String msg = notificationMessage.getMessage();
               System.out.println(msg);
           }
       }
    };
}
