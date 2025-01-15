import java.nio.file.Files;
import java.nio.file.Paths;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;


import java.io.OutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.InetAddress;

public class Webhttp implements Runnable {
    private InetAddress ip;
    private int port;

    // Constructeur d'instance
    public Web(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    // Méthode non statique pour démarrer le serveur HTTP
    public void run() {
        try {

            // Création du serveur HTTP
            HttpServer server = HttpServer.create(new InetSocketAddress(ip, port), 0);

            // Configuration des handlers pour différentes pages
            server.createContext("/", new RootHandler());
            server.createContext("/GUI.css", new CssHandler());


            server.setExecutor(null); // Utilise l'exécuteur par défaut
            server.start();

            System.out.println("Serveur HTTP en écoute sur le port 9999...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour charger le contenu d'un fichier
    public static String loadFileContent(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            return null; // Le fichier n'existe pas
        }
        return Files.readString(file.toPath()); // Java 11 ou plus récent
    }

    static class RootHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = loadFileContent("GUI.html");
            
            if (response == null) {
                String notFoundResponse = "404 - Fichier HTML non trouvé";
                exchange.sendResponseHeaders(404, notFoundResponse.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFoundResponse.getBytes());
                }
                return;
            }
    
            // Répondre avec le contenu HTML
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
    }

    // Handler pour "/GUI.css"
    static class CssHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String cssContent = loadFileContent("GUI.css");
            if (cssContent != null) {
                exchange.getResponseHeaders().set("Content-Type", "text/css");
                exchange.sendResponseHeaders(200, cssContent.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(cssContent.getBytes());
                os.close();
            } else {
                exchange.sendResponseHeaders(404, -1);
            }
        }
    }
}
