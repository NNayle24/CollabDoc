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
        Webhttp webServer = new Webhttp(ip,9999, passwordManager);
        Thread serverThread = new Thread(webServer);
        serverThread.start();

        Sender sender = new Sender( file);
        file.subscribe(sender);
        file.printSubscribers();
        

        // Lancement du serveur WebSockets
        WebsocketServeur websocketServer = new WebsocketServeur(ip, 8080, file, passwordManager);
        sender.setWebsocketServer(websocketServer);
        
        Thread websocketThread = new Thread(websocketServer);
        websocketThread.start();

        //Fermetures des service
        try {
            serverThread.join();
            websocketThread.join();
        } catch (InterruptedException e) {
            System.err.println("Thread interrupted: " + e.getMessage());
        }
    }
}