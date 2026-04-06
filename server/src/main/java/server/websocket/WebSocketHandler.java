package server.websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();

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
    public void handleMessage(@NotNull WsMessageContext ctx){
        UserGameCommand gameCommand = new Gson().fromJson(ctx.message(), UserGameCommand.class);
        switch (gameCommand.getCommandType()){
            case CONNECT -> enter();
            case LEAVE -> leave();
            case MAKE_MOVE -> makeMove();
            case RESIGN -> resign();
        }

    }

    private void enter(){}
    private void leave(){}
    private void makeMove(){}
    private void resign(){}
}
