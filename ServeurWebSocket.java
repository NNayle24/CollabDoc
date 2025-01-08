import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArrayList;

@ServerEndpoint("/ws")
public class ServeurWebSocket {
    private static final CopyOnWriteArrayList<Session> clients = new CopyOnWriteArrayList<>();
    private static final StringBuilder document = new StringBuilder();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Client connecté : " + session.getId());
        clients.add(session);
        try {
            session.getBasicRemote().sendText("DOCUMENT:" + document.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    @OnClose
    public void onClose(Session session) {
        clients.remove(session);
        System.out.println("Client déconnecté : " + session.getId());
    }
    
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Message reçu : " + message);
        try {
            if (message.startsWith("LOGIN:")) {
                String username = message.substring(6).trim();
                if (username.isEmpty()) {
                    session.getBasicRemote().sendText("LOGIN_FAILED: Username cannot be empty.");
                } else {
                    session.getBasicRemote().sendText("LOGIN_SUCCESS: Bienvenue, " + username + "!");
                    System.out.println("Utilisateur connecté : " + username);
                }
            } else if (message.startsWith("UPDATE:")) {
                String content = message.substring(7);
                synchronized (document) {
                    document.append(content);
                }
                broadcast("UPDATED:" + content);
            } else {
                session.getBasicRemote().sendText("UNKNOWN_COMMAND");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Erreur avec le client : " + session.getId());
        throwable.printStackTrace();
    }

    private void broadcast(String message) {
        for (Session client : clients) {
            try {
                client.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
