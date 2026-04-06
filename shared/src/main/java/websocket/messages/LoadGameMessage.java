package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage  extends  ServerMessage {

    private ChessGame game;

    public LoadGameMessage(ChessGame game){
        super(ServerMessage.ServerMessageType.LOAD_GAME);
        this.game = game;
    }

    public ChessGame getGame() {
        return game;
    }
}
