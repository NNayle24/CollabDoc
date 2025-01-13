import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.io.IOException;
import java.io.OutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import java.net.InetSocketAddress;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class ServeurMa {
    // Liste pour gérer les clients connectés via WebSocket
    private static final List<WebSocket> webSocketClients = new CopyOnWriteArrayList<>();
    private static String loadFileContent(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return null; // Le fichier n'existe pas
        }
        return Files.readString(file.toPath());
            
    }
    public static void main(String[] args) throws Exception {
        // Lancer le serveur HTTP
        Thread httpServerThread = new Thread(ServeurMa::startHttpServer);
        httpServerThread.start();

        // Lancer le serveur WebSocket
        startWebSocketServer();
    }

    // Serveur WebSocket
    public static void startWebSocketServer() {
        try {
            int port = 8080; // Port d'écoute
            WebSocketServer server = new WebSocketServer(new InetSocketAddress(port)) {
                @Override
                public void onOpen(WebSocket conn, ClientHandshake handshake) {
                    System.out.println("Nouvelle connexion : " + conn.getRemoteSocketAddress());
                    webSocketClients.add(conn);
                    conn.send("Bienvenue sur le serveur WebSocket!");
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

    // Serveur HTTP
    private static void startHttpServer() {
        try {
            int port = 9999; // Port d'écoute HTTP
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

            // Ajouter les routes
            server.createContext("/", new RootHandler());

            server.setExecutor(null); // Crée un thread par défaut
            server.start();
            System.out.println("Serveur HTTP démarré sur le port " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = loadFileContent("gui.html");
            if (response == null) {
                String notFoundResponse = "404 - Fichier non trouvé";
                exchange.sendResponseHeaders(404, notFoundResponse.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFoundResponse.getBytes());
                }
                return;
            }
    
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }
}


