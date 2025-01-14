import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebsocketServeur implements Runnable {
    private static final List<WebSocket> webSocketClients = new CopyOnWriteArrayList<>();
    private int port;
    private InetAddress adresse;

    WebsocketServeur(InetAddress ip, int port) {
        this.port = port;
        this.adresse = ip;
    }

    // Serveur WebSocket
    public void run() {
        try {
            // Utilisation des variables d'instance 'adresse' et 'port'
            WebSocketServer server = new WebSocketServer(new InetSocketAddress(adresse, port)) {
                @Override
                public void onOpen(WebSocket conn, ClientHandshake handshake) {
                    System.out.println("Nouvelle connexion : " + conn.getRemoteSocketAddress());
                    webSocketClients.add(conn);
                    conn.send("Bienvenue sur le serveur WebSocket!");
                    System.out.println("Connection ouverte avec : " + conn.getRemoteSocketAddress());
                }

                @Override
                public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                    System.out.println("Fermeture de la connexion : " + conn.getRemoteSocketAddress());
                    webSocketClients.remove(conn);
                }

                @Override
                public void onMessage(WebSocket conn, String message) {
                    System.out.println("Message reçu : " + message);
                    broadcastMessage("Message diffusé : " + message);
                }

                @Override
                public void onError(WebSocket conn, Exception ex) {
                    ex.printStackTrace();
                }

                @Override
                public void onStart() {
                    System.out.println("Serveur WebSocket démarré sur le port " + port);
                }
            };

            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Diffuse un message à tous les clients WebSocket connectés
    public static void broadcastMessage(String message) {
        for (WebSocket client : webSocketClients) {
            client.send(message);
        }
    }
}
