import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Message reçu: " + message);

                // Traitement des commandes
                if (message.startsWith("LOGIN")) {
                    // Traitement de connexion
                    String username = message.substring(6).trim();
                    if (username.isEmpty()) {
                        sendMessage("LOGIN_FAILED: Username cannot be empty.");
                    } else {
                        // Connexion réussie (vous pouvez ajouter une logique de validation si nécessaire)
                        sendMessage("LOGIN_SUCCESS");
                        // Envoyer le document partagé au nouveau client
                        sendMessage("DOCUMENT:" + Serveur.getDocument());
                        System.out.println("Utilisateur connecté : " + username);
                    }
                } else if (message.startsWith("UPDATE")) {
                    // Mise à jour du document
                    String content = message.substring(7);
                    Serveur.updateDocument(content);
                    // Diffuser la mise à jour à tous les clients
                    Serveur.broadcast(content);
                } else {
                    sendMessage("UNKNOWN_COMMAND");
                }
            }
        } catch (IOException e) {
            System.err.println("Erreur dans ClientHandler : " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }
}
