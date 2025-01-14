import java.net.InetAddress;
import java.net.UnknownHostException;
import org.java_websocket.server.WebSocketServer;

public class Main {
    static String Passwordfile = "passwords.txt";
    static String filedata = "data.txt";
    public static void main(String[] args) {
        InetAddress ip = null;
        try {
            ip = InetAddress.getLocalHost();
            System.out.println("Adresse IP: " + ip.getHostAddress());
        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + e.getMessage());
        }
        // Création de la sturcture de données du fichier
        File file = new File();
        file.LoadFileContent(filedata);

        // Chargement du fichier de mots de passe
        PasswordManager passwordManager = new PasswordManager("salt");
        passwordManager.loadFileContent(Passwordfile);

        // Lancement du serveur Web
        WebServeur webServer = new WebServeur(ip,9999);
        Thread serverThread = new Thread(webServer);
        serverThread.start();

        // Lancement du serveur WebSockets
        WebsocketServeur websocketServer = new WebsocketServeur(ip, 8080);
        Thread websocketThread = new Thread(websocketServer);
        websocketThread.start();

        // Création des services
        Sender sender = new Sender(websocketServer, file);
        file.subscribe(sender);

        //Fermetures des service
        try {
            serverThread.join();
            websocketThread.join();
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
        }
        file.SaveFileContent("data.txt");
        file.unsubscribe(sender);
    }
}