import java.io.*;
import java.net.Socket;

public class Client {
    private static boolean isLoggedIn = false; // Variable pour suivre si l'utilisateur est connecté

    public static void main(String[] args) {
        String host = "localhost";
        int port = 12345;

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.println("Veuillez vous connecter avec 'LOGIN:<username>' ou créer un compte avec 'CREATE:<username>:<password>'.");

            // Thread pour écouter les messages du serveur
            Thread listenerThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) { // Lire ligne par ligne
                        if (serverMessage.contains("LOGIN_SUCCESS")) {
                            // Si le message contient "LOGIN_SUCCESS"
                            isLoggedIn = true;
                            System.out.println("Connexion réussie !");
                        } else if (serverMessage.contains("DOCUMENT:")) {
                            // Si le message contient "DOCUMENT:"
                            System.out.println("Document reçu: " + serverMessage);
                        } else {
                            // Si c'est un autre message, l'afficher normalement
                            System.out.println(serverMessage);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Connexion au serveur perdue.");
                }
            });

            listenerThread.start();

            // Phase de connexion ou création de compte
            while (!isLoggedIn) {
                System.out.print("Saisissez votre commande (LOGIN ou CREATE) : ");
                String input = new BufferedReader(new InputStreamReader(System.in)).readLine();

                if (input.startsWith("LOGIN")) {
                    String content = input.substring(6);
                    out.println("LOGIN:" + content);
                } else if (input.startsWith("CREATE")) {
                    // Ajoutez une logique pour créer un compte si nécessaire
                    System.out.println("Création de compte...");
                } else {
                    System.out.println("Commande invalide. Essayez à nouveau.");
                }

                // Attendre que la connexion soit établie (vérifier que isLoggedIn devient true)
                while (!isLoggedIn) {
                    // Attente active pour permettre au listener de mettre à jour isLoggedIn
                    Thread.sleep(500);
                }
            }
            
            System.out.println("Tapez 'exit' pour quitter.");

            // Lecture des caractères immédiatement
            StringBuilder currentInput = new StringBuilder();
            while (true) {
                int character = System.in.read(); // Lecture immédiate des caractères

                if (character == -1) { // Fin d'entrée (en cas de EOF)
                    break;
                }

                // Si l'utilisateur tape 'e' et que ce n'est pas suivi de 'x' et 'i' (pour "exit")
                if (character == 'e' && currentInput.toString().equals("e")) {
                    System.out.println("Déconnexion...");
                    break;
                }

                // Si 'exit' est tapé
                if (character == 'e') {
                    currentInput.append((char) character);
                    continue;
                }

                if (character == 'x' && currentInput.toString().equals("e")) {
                    currentInput.append((char) character);
                    continue;
                }

                if (character == 'i' && currentInput.toString().equals("ex")) {
                    currentInput.append((char) character);
                    if (currentInput.toString().equals("exit")) {
                        System.out.println("Déconnexion...");
                        break;
                    }
                }

                // Ajouter le caractère au message actuel
                currentInput.append((char) character);

                // Envoyer le caractère au serveur
                out.println("UPDATE:" + (char) character);
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
