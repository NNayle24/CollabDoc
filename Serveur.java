import java.util.ArrayList;
import java.util.List;

public class Serveur {
    private static StringBuilder document = new StringBuilder();
    private static List<ClientHandler> clients = new ArrayList<>();

    public static synchronized String getDocument() {
        return document.toString();
    }

    public static synchronized void updateDocument(String content) {
        document.append(content);
    }

    public static synchronized void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public static synchronized void addClient(ClientHandler client) {
        clients.add(client);
    }

    public static synchronized void removeClient(ClientHandler client) {
        clients.remove(client);
    }
}
