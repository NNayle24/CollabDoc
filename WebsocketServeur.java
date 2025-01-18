import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import jdk.jshell.execution.Util;

public class WebsocketServeur implements Runnable {
    private static final List<WebSocket> webSocketClients = new CopyOnWriteArrayList<>();
    private final int port;
    private final InetAddress adresse;
    private final File file;
    private final PasswordManager passwordManager;

    WebsocketServeur(InetAddress ip, int port, File file, PasswordManager passwordManager) {
        this.passwordManager = passwordManager;
        this.port = port;
        this.adresse = ip;
        this.file = file;
    }
    
    // Main method to start the WebSocket server

    // Serveur WebSocket
    @Override
    public void run() {
        Sender sender = new Sender(this, file);
        try {
            // Utilisation des variables d'instance 'adresse' et 'port'
            WebSocketServer server = new WebSocketServer(new InetSocketAddress(adresse, port)) {
                @Override
                public void onOpen(WebSocket conn, ClientHandshake handshake) {
                    System.out.println("Nouvelle connexion : " + conn.getRemoteSocketAddress());
                    webSocketClients.add(conn);

                    
                    System.out.println("Envoi des liens existants");
                    Item[] items = file.getItems("first"); // Replace "someArgument" with the appropriate argument
                    System.out.println("Nombre de liens : " + items.length);

                    for (Item item : items) {
                        String formattedData = Utilities.AddLibelle(Utilities.ItemtoData(item), "NEW_LINK:");
                        if (formattedData != null) {
                            conn.send(formattedData);
                        } else {
                            System.err.println("Error formatting data for item: " + item);
                        }
                        }


                }

                @Override
                public void onClose(WebSocket conn, int code, String reason, boolean remote) {
                    System.out.println("Fermeture de la connexion : " + conn.getRemoteSocketAddress());
                    webSocketClients.remove(conn);
                }

                @Override
                public void onMessage(WebSocket conn, String message) {
                    System.out.println("Message reçu : " + message);

                    if (message.startsWith("NEW_LINK:")) {
                        System.out.println("Nouveau lien reçu : ");
                        // Message format : NEW_LINK:token,Author;cipher;date;lec;erc;iv;next;prev
                        String[] data = Utilities.decapsulate(message);
                        String token = Utilities.ExtractToken(data);
                        if (passwordManager.checkToken(token))
                        {
                            String[] itemData = Utilities.RmLibelle(data);
                            Item item = new Item(itemData);
                            item.print();
                            file.addItem(item);
                        }
                        else
                        {
                            System.out.println("Token invalide");
                        }


                    } else if (message.startsWith("REMOVE_LINK:")) {
                        System.out.println("Lien supprimé reçu : ");
                        // Message format : REMOVE_LINK:token,ID
                        String Id = message.substring(12);
                        String token = Id.split(",")[0];
                        Id = Id.split(",")[1];
                        if (passwordManager.checkToken(token))
                        {
                            file.removeItem(Id);
                        }
                        else
                        {
                            System.out.println("Token invalide");
                        }         
                        file.removeItem(Id);
  
                    } else {
                        System.out.println("Message non traité : " + message);
                    }
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
            System.out.println("Serveur WebSocket accessible a l'url complete : ws://" + adresse.getHostAddress() + ":" + port);
        } catch (Exception e) {
            System.err.println("Erreur lors du démarrage du serveur WebSocket : " + e.getMessage());
        }
    }

    // Diffuse un message à tous les clients WebSocket connectés
    public static  void broadcast(String message) {
        for (WebSocket client : webSocketClients) {

            client.send(message);
        }
    }

    public static void broadcast(String[] messages) {
        for (String message : messages) {
            broadcast(message);
        }
    }

    public static void broadcast(String []message ,String libelle) {
        if (libelle.equals("NEW_LINK:"))
        {
            for (String m : message) {
                broadcast(m);
            }
        }
        else if (libelle.equals("REMOVE_LINK:"))
        {
           for (String m : message) {
                String formattedMessage = libelle + m;
                broadcast(formattedMessage);
           }
        }
    }

    public static void broadcast(Item item,String libelle) {
        if ("NEW_LINK:".equals(libelle))
        {
            String[] data = Utilities.ItemtoData(item);
            String message = Utilities.AddLibelle(data, libelle);
            broadcast(message);
        }
        else if (libelle.equals("REMOVE_LINK:"))
        {
            String message = libelle +":"+ item.getId();
            broadcast(message);
        }

    }

    public static void broadcast(Item[] item,String libelle) {
        for (Item i : item) {
            broadcast(i, libelle);
        }
    }

}
