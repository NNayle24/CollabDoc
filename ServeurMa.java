
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.SSLContext;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.HttpsConfigurator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;


public class ServeurMa {
    // Liste des clients WebSocket connectés
    private static final List<WebSocket> webSocketClients = new CopyOnWriteArrayList<>();

    private static String loadFileContent(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return null; // Le fichier n'existe pas
        }
        return Files.readString(file.toPath());
            
    }

    public static void main(String[] args) throws Exception {
        // Démarrer le serveur HTTPS dans un thread
        Thread httpsServerThread = new Thread(ServeurMa::startHttpsServer);
        httpsServerThread.start();

        // Démarrer le serveur WebSocket
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

    // Lancer le serveur HTTPS
    private static void startHttpsServer() {
        try {
            char[] password = "polytech".toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(Files.newInputStream(Paths.get("keystore.jks")), password);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            // Création du serveur HTTPS
            HttpsServer server = HttpsServer.create(new InetSocketAddress(9999), 0);
            server.setHttpsConfigurator(new HttpsConfigurator(sslContext));

            // Configuration des handlers pour différentes pages
            server.createContext("/", new RootHandler());

            server.setExecutor(null); // Utilise l'exécuteur par défaut
            server.start();

            System.out.println("Serveur HTTPS en écoute sur le port 9999...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Gestion des requêtes HTTP
    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = loadFileContent("y.html");
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


