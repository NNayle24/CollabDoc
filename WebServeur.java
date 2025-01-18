import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class WebServeur implements Runnable {
    private InetAddress ip;
    private int port;
    private static PasswordManager PM;

    // Constructeur d'instance
    public WebServeur(InetAddress ip, int port, PasswordManager pm) {
        this.ip = ip;
        this.port = port;
        this.PM=pm; 
    }

    // Méthode non statique pour démarrer le serveur HTTPS
    public void run() {
        try {
            // Créer un serveur HTTPS
            HttpsServer server = HttpsServer.create(new InetSocketAddress(ip,port), 0);
            // Créer un contexte SSL
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // Initialiser le contexte SSL
            char[] password = "polytech".toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(Files.newInputStream(Paths.get("keystore.jks")), password);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            // Configurer le serveur HTTPS
            server.setHttpsConfigurator(new HttpsConfigurator(sslContext));

            // Créer un gestionnaire de racine
            server.createContext("/", new LoginHandler());
            server.createContext("/log.css", new logHandler());
            server.createContext("/GUI.css", new CssHandler());

            // Démarrer le serveur
            System.out.println("Démarrage du serveur HTTPS pour ce connecter à l'adresse : https://" + ip.getHostAddress() + ":" + port);
            server.start();
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

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
               
                String formData = new String(exchange.getRequestBody().readAllBytes());
                String[] params = formData.split("&");
    
                String login = null;
                String mdp = null;
    
              
                for (String param : params) {
                    String[] pair = param.split("=");
                    if (pair.length == 2) {
                        if ("login".equals(pair[0])) {
                            login = pair[1];
                        } else if ("mdp".equals(pair[0])) {
                            mdp = pair[1];
                        }
                    }
                }
                
                String userInfo = PM.verify(login, mdp);
    
                if (userInfo != "") {
                    Map<String, String> placeholders = new HashMap<>();
                    String [] info = userInfo.split(",");
                    if (info.length == 3) {
                        placeholders.put("lec", info[0]);
                        placeholders.put("ecr", info[1]);
                        info = info[2].split(":");
                        placeholders.put("key", info[0]);  
                        placeholders.put("token", info[1]);
                    }
                    String response = loadFileContent("GUI.html");
                    if (response == null) {
                        response = "404 - Page de login introuvable";
                    }
                    response = replacePlaceholders(response, placeholders);
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(401, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                } else {
                    
                    String response = loadFileContent("log.html");
                    if (response == null) {
                        response = "404 - Page de login introuvable";
                    }
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(401, response.getBytes().length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
                }
            } else {
                
                String response = loadFileContent("log.html");
                if (response == null) {
                    response = "404 - Page de login introuvable";
                }
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            }
        }
    }
    

    static class logHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String cssContent = loadFileContent("log.css");
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

    //Handler pour "/GUI.css"
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

    // Méthode pour remplacer les variables dans le fichier HTML
    public static String replacePlaceholders(String htmlContent, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String placeholder = "#var#" + entry.getKey() + "#var#";
            htmlContent = htmlContent.replace(placeholder, entry.getValue());
        }
        return htmlContent;
    }
}
