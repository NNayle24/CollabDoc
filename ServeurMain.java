import org.glassfish.tyrus.server.Server;

import java.util.HashMap;
import java.util.Map;

public class ServeurMain {
    public static void main(String[] args) {
        // Paramètres du serveur
        String ip = "172.20.164.24";
        int port = 8080;
        String path = "/ws";
        String uri = "ws://" + ip + ":" + port + path;

        // Configuration du serveur
        Map<String, Object> config = new HashMap<>();
        Server server = new Server(ip, port, path, config, ServeurWebSocket.class);

        try {
            System.out.println("Serveur WebSocket démarré à l'adresse : " + uri);
            server.start();
            // Garder le serveur actif
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }
}
