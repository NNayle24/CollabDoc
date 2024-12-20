import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServeurMain {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Serveur en écoute sur le port 12345...");

            while (true) {
                // Attendre une connexion d'un client
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nouveau client voulant se connecté : " + clientSocket.getInetAddress());

                // Créer un handler pour gérer la communication avec le client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                
                // Ajouter le client à la liste des clients
                Serveur.addClient(clientHandler);
                
                // Lancer un thread pour gérer la communication
                new Thread(clientHandler).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
