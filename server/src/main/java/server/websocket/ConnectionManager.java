package server.websocket;



import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import websocket.messages.ServerMessage;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, ConcurrentHashMap<String, Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, String username, Session session){
        connections.computeIfAbsent(gameID, k -> new ConcurrentHashMap<>()).put(username, session);

    }

    public void remove(int gameID, String username){
        var inner = connections.get(gameID);
        if(inner != null){
            inner.remove(username);
        }

    }

    public void sendMessage(int gameID, String username, ServerMessage message) throws IOException {
        String msg = new Gson().toJson(message);
        var inner = connections.get(gameID);

        if(inner != null){
            Session session = inner.get(username);

            if(session != null && session.isOpen()){
                session.getRemote().sendString(msg);
            }
        }
    }

    public void broadcastMessage(int gameID, String skipUser, ServerMessage message) throws IOException{
        String msg = new Gson().toJson(message);
        var inner = connections.get(gameID);

        if(inner != null){
            for(var entry : inner.entrySet()){
                String user = entry.getKey();
                Session session = entry.getValue();

                if(session.isOpen() && !user.equals(skipUser)){
                    session.getRemote().sendString(msg);
                }
            }
        }
    }
}
